
package fruit.health.server.endpoints.gwt;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import fruit.health.client.rpc.UserService;
import fruit.health.server.bizlogic.UserMgmtBizLogic;
import fruit.health.server.util.CommunicationChannel;
import fruit.health.shared.dto.LoginInfo;
import fruit.health.shared.entities.User;
import fruit.health.shared.exceptions.InvalidLoginCredsException;
import fruit.health.shared.exceptions.NoSuchEntityException;
import fruit.health.shared.exceptions.PasswordTooWeakException;
import fruit.health.shared.exceptions.UserExistsException;
import fruit.health.shared.exceptions.UserNotVerifiedException;
import fruit.health.shared.exceptions.VerificationExpiredException;

@SuppressWarnings("serial")
@Singleton
public class UserServiceImpl extends CsrfSafeRemoteServiceServlet implements UserService
{
    private final UserMgmtBizLogic userMgmtBizLogic;

    @Inject
    UserServiceImpl (UserMgmtBizLogic userMgmtBizLogic)
    {
        this.userMgmtBizLogic = userMgmtBizLogic;
    }

    @Override
    public LoginInfo login (String email, String password, boolean rememberMe)
            throws InvalidLoginCredsException, UserNotVerifiedException
    {
        return userMgmtBizLogic.login(email, password, rememberMe,
                getThreadLocalRequest(), getThreadLocalResponse(), CommunicationChannel.GWT_RPC);
    }

    @Override
    public LoginInfo registerUser (User newUserDetails, String password) throws UserExistsException, InvalidLoginCredsException, PasswordTooWeakException
    {
        return userMgmtBizLogic.registerUser(newUserDetails, password,
                getThreadLocalRequest(), getThreadLocalResponse(), CommunicationChannel.GWT_RPC);
    }

    @Override
    public void forgotPassword (String email, String urlBase)
            throws InvalidLoginCredsException, NoSuchEntityException
    {
        userMgmtBizLogic.forgotPassword(email, urlBase,
                getThreadLocalRequest(), CommunicationChannel.GWT_RPC);
    }

    @Override
    public LoginInfo resetPassword (String newPassword, String verifier)
            throws InvalidLoginCredsException, NoSuchEntityException,
            VerificationExpiredException
    {
        return userMgmtBizLogic.resetPassword(newPassword, verifier,
                getThreadLocalRequest(), getThreadLocalResponse(), CommunicationChannel.GWT_RPC);
    }

    @Override
    public LoginInfo changeEmailVerification (Long userId, String newEmail,
            String verifier) throws NoSuchEntityException,
            InvalidLoginCredsException, VerificationExpiredException
    {
        return userMgmtBizLogic.changeEmailVerification(userId, newEmail, verifier,
                getThreadLocalRequest(), getThreadLocalResponse(), CommunicationChannel.GWT_RPC);
    }
}
