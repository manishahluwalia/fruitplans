
package fruit.health.server.endpoints.gwt;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fruit.health.server.util.Utils;
import fruit.health.shared.dto.LoginInfo;
import fruit.health.shared.util.SharedConstants;


// See http://jectbd.com/?p=1351
@SuppressWarnings("serial")
public class CsrfSafeRemoteServiceServlet extends BaseRemoteServiceServlet
{
    private static final Logger logger = LoggerFactory.getLogger(CsrfSafeRemoteServiceServlet.class);

    @Override
    protected void onBeforeRequestDeserialized (String serializedRequest)
    {
        super.onBeforeRequestDeserialized(serializedRequest);

        HttpServletRequest request = getThreadLocalRequest();        
        HttpSession session = request.getSession(false);
        String guardHeaderId = request.getHeader(SharedConstants.CSRF_GUARD_HEADER);
        if (null != session)
        {
            String sessionId = session.getId();
            if (sessionId.equals(guardHeaderId))
            {
                return;
            }

            String rpcName = getRpcName(serializedRequest);
            logger.error(Utils.dumpRequestMetadata(request)+ ": Mismatched CSRF guard header for request: " + rpcName);
            throw new SecurityException("RPC request from IP " + request.getRemoteAddr() + " with mismatched CSRF guard: sessionId:" + sessionId + " requestHeader:" + guardHeaderId + " request:" + rpcName);
        }
        else
        {
            // TODO: RELOGIN NOT IMPLEMENTED YET!

            String rpcName = getRpcName(serializedRequest);
            
            logger.error(Utils.dumpRequestMetadata(request)+ ": No Session found for request: " + rpcName);
            throw new SecurityException("RPC request with new session");
        }
    }

    protected LoginInfo getCreds ()
    {
        HttpSession session = getThreadLocalRequest().getSession(false);
        return Utils.getCredsFromSession(session);
    }
}
