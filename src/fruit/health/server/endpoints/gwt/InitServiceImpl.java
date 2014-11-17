
package fruit.health.server.endpoints.gwt;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import fruit.health.client.rpc.InitService;
import fruit.health.server.auditlog.AuditEvent;
import fruit.health.server.auditlog.AuditLogger;
import fruit.health.server.bizlogic.UserMgmtBizLogic;
import fruit.health.server.logging.SessionLoggingLevelTracker;
import fruit.health.server.util.CommunicationChannel;
import fruit.health.server.util.Constants;
import fruit.health.server.util.CookieUtils;
import fruit.health.server.util.GoogleAnalytics;
import fruit.health.server.util.ServerProperties;
import fruit.health.server.util.Utils;
import fruit.health.shared.dto.InitInfo;
import fruit.health.shared.dto.LoginInfo;
import fruit.health.shared.entities.User;
import fruit.health.shared.util.BrowserIdCookie;
import fruit.health.shared.util.InlineMap;
import fruit.health.shared.util.Pair;
import fruit.health.shared.util.SharedConstants;

@SuppressWarnings("serial")
@Singleton
public class InitServiceImpl extends BaseRemoteServiceServlet implements InitService
{
    private static final AuditLogger auditLogger = new AuditLogger();
    private static final Logger logger = LoggerFactory.getLogger(InitServiceImpl.class);

    private final UserMgmtBizLogic userMgmtBizLogic;

    @Inject
    InitServiceImpl (UserMgmtBizLogic userMgmtBizLogic)
    {
        this.userMgmtBizLogic = userMgmtBizLogic;
    }

    @Override
    public Pair<InitInfo, LoginInfo> initClient (final String referer, final String serverLogging)
    {
        final HttpServletRequest request = getThreadLocalRequest();
        HttpServletResponse response = getThreadLocalResponse();

        String visitId = Utils.generateTimestampedId();
        MDC.put("visitId", visitId);

        SessionLoggingLevelTracker.setLevelForSession(serverLogging, request.getSession(true));

        auditLogger.log(AuditEvent.APP_ACCESSED, new InlineMap() {{
            _("userAgent", request.getHeader("User-Agent"));
            _("sourceIP", request.getRemoteAddr());
            _("referrer", referer);
        }});

        InitInfo initInfo = new InitInfo();
        initInfo.setCommitId(ServerProperties.getCommitId());
        initInfo.setVisitId(visitId);
        initInfo.setGoogleAnalyticsId(GoogleAnalytics.getAccountId());

        LoginInfo loginInfo = userMgmtBizLogic.autoLoginIfPossible(
                getThreadLocalRequest(), getThreadLocalResponse(), CommunicationChannel.GWT_RPC);

        // create a session if not already created and verify/set the browser cookie
        // creating the session must happen after call to autoLoginIfPossible so as not to break assumptions in that call
        request.getSession(true);
        Cookie browserCookie = CookieUtils.getCookie(request, SharedConstants.BROWSER_COOKIE_NAME);
        BrowserIdCookie browserIdCookie = null;
        if (null != browserCookie)
        {
            try
            {
                browserIdCookie = new BrowserIdCookie(browserCookie.getValue());
            }
            catch (Exception e)
            {
                logger.debug("client came in with invalid browser id cookie: " + browserCookie.getValue());
            }
        }

        if (null == browserIdCookie)
        {
            browserIdCookie = new BrowserIdCookie();
            final String browserId = Utils.randomHexString(User.BROWSER_AUTH_TOKEN_BYTES);
            browserIdCookie.setBrowserId(browserId);
            auditLogger.log(AuditEvent.BROWSER_ID_GENERATED, new InlineMap() {{
                _("browserId", browserId);
            }});
        }

        MDC.put("browser", browserIdCookie.getBrowserId());
        CookieUtils.setCookie(response, SharedConstants.BROWSER_COOKIE_NAME, browserIdCookie.serialize(), Constants.BCOOKIE_AGE);

        Pair<InitInfo, LoginInfo> result = new Pair<InitInfo, LoginInfo>();
        result.setA(initInfo);
        result.setB(loginInfo);

        return result;
    }
}
