
package fruit.health.server.endpoints.gwt;


import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fruit.health.server.bizlogic.RequestContextHolder;
import fruit.health.server.util.Utils;
import fruit.health.shared.util.SharedConstants;


@SuppressWarnings("serial")
public abstract class AuthenticatedRemoteServiceServlet extends CsrfSafeRemoteServiceServlet
{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticatedRemoteServiceServlet.class);

    @Override
    protected void onBeforeRequestDeserialized (String serializedRequest)
    {
        super.onBeforeRequestDeserialized(serializedRequest);
        // TODO fix logic below
        assert null!=getCreds() : "No LoginInfo in session";
        if (null!=getCreds().getUser())
        {
            RequestContextHolder.setCreds(getCreds());
            return;
        }

        String rpcName = getRpcName(serializedRequest);
        logger.error(Utils.dumpRequestMetadata(getThreadLocalRequest()) + ": Need to authenticate before executing " + rpcName);

        HttpServletResponse response = this.getThreadLocalResponse();
        response.setHeader(SharedConstants.AUTHENTICATION_NEEDED_HEADER, "true");
        try
        {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        catch (IOException e)
        {
            logger.error("Can't sendError", e);
        }
        try
        {
            response.getOutputStream().flush();
        }
        catch (IOException e)
        {
            logger.error("Can't getOutputStream", e);
        }
        catch (IllegalStateException e)
        {
            // In the unlikely event that someone has already asked for a
            // PrintWriter
            try
            {
                response.getWriter().flush();
            }
            catch (IOException e1)
            {
                logger.error("Can't getWriter", e);
            }
        }

        throw new SecurityException("Unauthenticated request " + rpcName);
    }
}
