
package fruit.health.server.bizlogic;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.Stage;

import fruit.health.server.auditlog.AuditEvent;
import fruit.health.server.auditlog.AuditLogger;
import fruit.health.server.cloner.impl.RecursiveReflexiveCloner;
import fruit.health.server.dao.iface.ConsolidatedDao;
import fruit.health.server.dao.iface.Transaction;
import fruit.health.server.dao.iface.TransactionFactory;
import fruit.health.server.logging.SessionLoggingLevelTracker;
import fruit.health.server.util.CommunicationChannel;
import fruit.health.server.util.Constants;
import fruit.health.server.util.CookieUtils;
import fruit.health.server.util.Utils;
import fruit.health.shared.dto.LoginInfo;
import fruit.health.shared.dto.LoginInfo.LoginMethod;
import fruit.health.shared.entities.User;
import fruit.health.shared.exceptions.InvalidLoginCredsException;
import fruit.health.shared.exceptions.NoSuchEntityException;
import fruit.health.shared.exceptions.PasswordTooWeakException;
import fruit.health.shared.exceptions.UserExistsException;
import fruit.health.shared.exceptions.UserNotVerifiedException;
import fruit.health.shared.exceptions.VerificationExpiredException;
import fruit.health.shared.util.DeserializationHelper;
import fruit.health.shared.util.InlineMap;
import fruit.health.shared.util.SerializationHelper;
import fruit.health.shared.util.SharedUtils;
import fruit.health.shared.util.DeserializationHelper.IncompleteData;
import fruit.health.shared.util.DeserializationHelper.UnsupportedVersion;

@Singleton
public class UserMgmtBizLogic
{
    public static class RememberMeToken
    {
        private static final int OBJECT_VERSION = 1;

        private Long userId;
        private String rememberMeToken;

        public RememberMeToken ()
        {
        }

        public RememberMeToken (String serialized) throws IncompleteData, UnsupportedVersion
        {
            DeserializationHelper helper = new DeserializationHelper(serialized);

            if (OBJECT_VERSION < helper.getObjectVersion())
            {
                throw new DeserializationHelper.UnsupportedVersion(Integer.toString(helper.getObjectVersion()));
            }

            userId = helper.readLong();
            rememberMeToken = helper.readString();
        }

        public String serialize ()
        {
            SerializationHelper helper = new SerializationHelper(OBJECT_VERSION);

            assert null!=userId;
            assert null!=rememberMeToken;
            assert !rememberMeToken.isEmpty();

            helper.writeLong(userId);
            helper.writeString(rememberMeToken);

            return helper.getSerialized();
        }

        /**
         * @return the {@link #userId}
         */
        public Long getUserId ()
        {
            return userId;
        }

        /**
         * @param userId the {@link #userId} to set
         */
        public void setUserId (Long userId)
        {
            this.userId = userId;
        }

        /**
         * @return the {@link #rememberMeToken}
         */
        public String getRememberMeToken ()
        {
            return rememberMeToken;
        }

        /**
         * @param rememberMeToken the {@link #rememberMeToken} to set
         */
        public void setRememberMeToken (String rememberMeToken)
        {
            this.rememberMeToken = rememberMeToken;
        }
    }

    // TODO: stub classes until real logic is ported
    public static class Notification { }
    public static class UserPublicInfo { }
    public static class UserUpdateInfo { }

    private static final Logger logger = LoggerFactory.getLogger(UserMgmtBizLogic.class);

    private static final int SESSION_TRACKER_BYTES = 8;

    //private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * The number of milliseconds for which a verification token is valid
     */
    private static final long VERFICATION_EXPIRATION_DURATION = 1000 * 60 * 60 * 24 * 3; // 3 days

    private final TransactionFactory txFactory;
    private final ConsolidatedDao dao;
    private final RecursiveReflexiveCloner cloner;
    //private final Notification notification;
    private final AuditLogger auditLogger;

    @Inject
    UserMgmtBizLogic (TransactionFactory txFactory, ConsolidatedDao dao, RecursiveReflexiveCloner cloner,
            Notification notification, AuditLogger auditLogger, Stage stage)
    {
        this.txFactory = txFactory;
        this.dao = dao;
        this.cloner = cloner;
        //this.notification = notification;
        this.auditLogger = auditLogger;
    }

    /**
     * @param commChannel How is this login happening
     * @param request The {@link HttpServletRequest} which made this call. The session associated with this request may be modified.
     * @see UserService#getMyInfo()
     */
    public LoginInfo autoLoginIfPossible (HttpServletRequest request, HttpServletResponse response, CommunicationChannel commChannel)
    {
        HttpSession session = request.getSession(true);

        LoginInfo loginInfo = (LoginInfo)session.getAttribute(Constants.SESSION_LOGIN_INFO_ATTRIBUTE);

        // Check if we can login via remember me
        Cookie rememberMeCookie = CookieUtils.getCookie(request, Constants.TEMPORARY_REMEMBER_ME_COOKIE_NAME);
        if (null == rememberMeCookie)
        {
            logger.debug("autoLoginIfPossible does not have a temporary rememberMe cookie");
        }
        else
        {
            try
            {
                RememberMeToken rememberMe = new RememberMeToken(rememberMeCookie.getValue());

                Transaction tx = txFactory.get();
                try
                {
                    User user = dao.getUserById(rememberMe.getUserId());
                    if (null == user)
                    {
                        logger.debug("autoLoginIfPossible did not get a valid user in temporary rememberMe cookie");
                    }
                    else
                    {
                        logger.debug("autoLoginIfPossible got a valid user in temporary rememberMe cookie");

                        loginInfo = _doLogin(user, false, request, response, LoginMethod.REMEMBER_ME, commChannel);

                        user.setLastLogin(new Date());
                        dao.update(user);

                        tx.commit();
                    }
                }
                finally
                {
                    tx.rollbackIfActive();
                }
            }
            catch (Exception e)
            {
                logger.info("autoLoginIfPossible: Invalid remember me cookie token: " + rememberMeCookie.getValue(), e);
            }
        }

        return loginInfo;
    }

    /**
     * The client has gotten a server error, and thinks that it could be due to
     * a server restart. Try to relog the client in based on any information we
     * may have in the request.
     *
     * @param currentVersion The current version of the client.
     * @param commChannel How is this login happening
     * @param request The {@link HttpServletRequest} which made this call. The session associated with this request may be modified.
     * @param response The {@link HttpServletResponse} for the call. Cookies will be set in response.
     * @return null if the user doesn't need to do anything -- the login is
     *         valid. Otherwise, a new LoginInfo if the user was successfully
     *         logged in.
     * @throws InvalidLoginCredsException Can't log client in based on current
     *             cookies
     */
    public LoginInfo relogin (int currentVersion, HttpServletRequest request, HttpServletResponse response, CommunicationChannel commChannel) throws InvalidLoginCredsException
    {
        HttpSession session = request.getSession(false);
        if (null != session)
        {
            if (null != session.getAttribute(Constants.SESSION_LOGIN_INFO_ATTRIBUTE))
            {
                return null;
            }
        }
        else
        {
            // create session
            request.getSession(true);
        }

        LoginInfo info = autoLoginIfPossible(request, response, commChannel);
        if (null == info)
        {
            throw new InvalidLoginCredsException();
        }

        auditLogger.log(AuditEvent.USER_RELOGIN);

        return info;
    }

    /**
     * @see UserService#login(String, String, boolean)
     * @param commChannel Set to {@link CommunicationChannel#GWT_RPC} if the call is made via GWT-RPC.
     * Set to {@link CommunicationChannel#JSON} if the call is made via JSON.
     */
    public LoginInfo login (String email, String password, boolean rememberMe, HttpServletRequest request, HttpServletResponse response, CommunicationChannel commChannel) throws InvalidLoginCredsException, UserNotVerifiedException
    {
        User user = dao.getUserByEmail(email);
        if (null == user)
        {
            throw new InvalidLoginCredsException();
        }

        _validatePassword(user, password);

        // save the last login time
        Transaction tx = txFactory.get();
        try
        {
            user.setLastLogin(new Date());
            dao.update(user);
            tx.commit();
        }
        finally
        {
            tx.rollbackIfActive();
        }

        return _doLogin(user, rememberMe, request, response, LoginMethod.PASSWORD, commChannel);
    }

    private void _validatePassword (final User user, String password) throws InvalidLoginCredsException
    {
        final String computedHash;
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(user.getSalt().getBytes(Constants.UTF8));
            computedHash = Utils.bytesToHexString(md.digest(password.getBytes(Constants.UTF8)));
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }

        if (!computedHash.equals(user.getPwdHash()))
        {
            auditLogger.log(AuditEvent.SECURITY_FAILED_LOGIN_ATTEMPT, new InlineMap() {{
                _("email", user.getEmail());
            }});
            throw new InvalidLoginCredsException();
        }
        // Everything is ok
    }

    private void _setRememberMeCookie (User user, boolean rememberMe, HttpServletRequest request, HttpServletResponse response)
    {
        RememberMeToken rememberMeToken = new RememberMeToken();
        rememberMeToken.setUserId(user.getUserId());
        rememberMeToken.setRememberMeToken(user.getRememberMeToken());

        String rememberMeString = rememberMeToken.serialize();
        CookieUtils.setCookie(response, Constants.TEMPORARY_REMEMBER_ME_COOKIE_NAME, rememberMeString);
        if (rememberMe)
        {
            CookieUtils.setPersistentCookie(response, Constants.REMEMBER_ME_COOKIE_NAME, rememberMeString);
        }
    }

    private LoginInfo _doLogin (User user, boolean rememberMe, HttpServletRequest request, HttpServletResponse response,
            final LoginMethod loginMethod, CommunicationChannel commChannel)
    {
        if (null == user.getUserId())
        {
            throw new NullPointerException("User entity not yet persisted?");
        }

        LoginInfo loginInfo = new LoginInfo();

        // This is a deep clone, not a GWT clone, since the loginInfo object will be persisted in the session object whose
        // lifetime is longer than that of the GWT-RPC that may have invoked us. Moreover, the session lives for longer than
        // the httprequest, which determines the lifetime of the entity manager which we must detach from.
        loginInfo.setUser(cloner.deepClone(user));
        loginInfo.setLoginMethod(loginMethod);
        loginInfo.setSessionTracker(Utils.randomHexString(SESSION_TRACKER_BYTES));

        request.getSession().invalidate(); // To prevent session fixation attack

        HttpSession session = request.getSession();
        session.setAttribute(Constants.SESSION_LOGIN_INFO_ATTRIBUTE, loginInfo);
        SessionLoggingLevelTracker.restoreLoggingLevelForNewSession(session);
        Utils.clearCreds();

        _setRememberMeCookie(user, rememberMe, request, response);

        MDC.put("userId", Long.toString(user.getUserId()));
        MDC.put("session", loginInfo.getSessionTracker());

        auditLogger.log(AuditEvent.USER_LOGIN, new InlineMap() {{
            _("loginMethod", loginMethod);
        }});

        return loginInfo;
    }

    /**
     * @see UserService#registerUser(String, String, UserProfile, String, String, String, String, String)
     * @param commChannel Set to {@link CommunicationChannel#GWT_RPC} if the call is made via GWT-RPC.
     * Set to {@link CommunicationChannel#JSON} if the call is made via JSON.
     */
    public LoginInfo registerUser (User newUserDetails, String password, HttpServletRequest request, HttpServletResponse response, CommunicationChannel commChannel) throws UserExistsException, InvalidLoginCredsException, PasswordTooWeakException
    {
        // TODO: can this be faked by running requests through a local proxy?
        // restrict signup to dev mode or admin users
        if (!request.getRemoteAddr().equals("127.0.0.1"))
        {
            try
            {
                if (!UserServiceFactory.getUserService().isUserAdmin())
                {
                    throw new InvalidLoginCredsException("invalid privileges");
                }
            }
            catch (Exception e)
            {
                throw new InvalidLoginCredsException("invalid privileges");
            }
        }

        if (null == newUserDetails)
        {
            throw new InvalidLoginCredsException("null user info");
        }

        String email = newUserDetails.getEmail();
        
        if (SharedUtils.isEmpty(email))
        {
            throw new InvalidLoginCredsException("empty email");
        }

        if (!SharedUtils.isValidEmail(email))
        {
            throw new IllegalArgumentException("invalid email");
        }

        String verifier;
        User user;
        Transaction tx = txFactory.get();
        boolean exists = false;
        try
        {
            Date now = new Date();

            user = dao.getUserByEmail(email);
            if (null != user)
            {
                if (!user.getVerificationExpiration().before(now))
                {
                    throw new UserExistsException();
                }
                exists = true;
            }

            verifier =  Utils.randomHexString(User.VERIFICATION_TOKEN_BYTES);

            if (!exists)
            {
                user = new User();
            }

            cloner.shallowCopyFieldsFromClient(user, newUserDetails);
            
            user.setSalt(email);
            user.setWhenCreated(now);
            _setUserPassword(user, password);
            user.setVerificationToken(verifier);
            user.setVerificationExpiration(new Date(now.getTime() + VERFICATION_EXPIRATION_DURATION));
            if (!exists)
            {
                dao.createNewUser(user);
            }
            else
            {
                dao.update(user);
            }

            tx.commit();
        }
        finally
        {
            tx.rollbackIfActive();
        }

        final Long userId = user.getUserId();
        auditLogger.log(AuditEvent.USER_SIGNUP, new InlineMap() {{
            _("userId", userId);
        }});

        // TODO: rememberMe handling for signup case
        return _doLogin(user, false, request, response, LoginMethod.SIGNUP, commChannel);
    }

    private void _setUserPassword (User user, String password)
    {
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        md.update(user.getSalt().getBytes(Constants.UTF8));
        user.setPwdHash(Utils.bytesToHexString(md.digest(password.getBytes(Constants.UTF8))));
        user.setRememberMeToken(Utils.randomHexString(User.REMEMBER_ME_TOKEN_BYTES));
    }

    /**
     * @see AuthenticatedUserService#changePassword(String, String)
     */
    public void changePassword (String oldPassword, String newPassword, HttpServletRequest request, HttpServletResponse response) throws InvalidLoginCredsException, PasswordTooWeakException
    {
    }

    /**
     * @param commChannel Set to {@link CommunicationChannel#GWT_RPC} if the call is made via GWT-RPC.
     * Set to {@link CommunicationChannel#JSON} if the call is made via JSON.
     * @see UserService#forgotPassword(String, String, String, String)
     */
    public void forgotPassword (String email, String urlBase, HttpServletRequest threadLocalRequest, CommunicationChannel commChannel) throws InvalidLoginCredsException, NoSuchEntityException
    {
        throw new NoSuchEntityException();
    }

    /**
     * @param commChannel Set to {@link CommunicationChannel#GWT_RPC} if the call is made via GWT-RPC.
     * Set to {@link CommunicationChannel#JSON} if the call is made via JSON.
     * @see UserService#resetPassword(String, String)
     */
    public LoginInfo resetPassword (String newPassword, String verifier, HttpServletRequest request, HttpServletResponse response, CommunicationChannel commChannel) throws InvalidLoginCredsException, NoSuchEntityException, VerificationExpiredException
    {
        return null;
    }

    /**
     * @param creds the current logged user
     * @see AuthenticatedUserService#changeEmail(String)
     */
    public void changeEmail (LoginInfo creds, String urlBase, HttpServletRequest request)
    {
    }

    /**
     * @see UserService#changeEmailVerification(Long, String, String)
     */
    public LoginInfo changeEmailVerification (Long userId, String newEmail, String verifier, HttpServletRequest request, HttpServletResponse response, CommunicationChannel gwtRpc) throws NoSuchEntityException, InvalidLoginCredsException, VerificationExpiredException
    {
        return null;
    }

    /**
     * @see UserService#getUserPublicInfo(Long)
     */
    public UserPublicInfo getUserPublicInfo (Long userId)
    {
        return null;
    }

    /**
     * @param commChannel How this login / logout is happening
     * @param request The request object. Used to check for existing cookies.
     * @param response The response object. Use to set cookies.
     * @see UserService#logout()
     */
    public LoginInfo logout (HttpServletRequest request, HttpServletResponse response, CommunicationChannel commChannel)
    {
        return null;
    }
}
