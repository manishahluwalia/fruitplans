
package fruit.health.server.endpoints.gwt;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import fruit.health.client.rpc.RepeatingCsrfSafeRpcBuilder;
import fruit.health.server.logging.LoggingUtils;
import fruit.health.server.util.Utils;


@SuppressWarnings("serial")
public abstract class BaseRemoteServiceServlet extends RemoteServiceServlet
{
    private static final Logger logger = LoggerFactory.getLogger(BaseRemoteServiceServlet.class);
    
    @Override
    protected void onBeforeRequestDeserialized (String serializedRequest)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Beginning: " + Utils.dumpRequestMetadata(getThreadLocalRequest()) + " " + getRpcName(serializedRequest));
        }
        
        super.onBeforeRequestDeserialized(serializedRequest);
    }

    @Override
    protected void onAfterRequestDeserialized (RPCRequest rpcRequest)
    {
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("Entering GWT-RPC: " + rpcRequest.getMethod().getDeclaringClass().getName() + "." + rpcRequest.getMethod().getName());
            if (logger.isTraceEnabled())
            {
                sb.append("  ");
                sb.append(Utils.dumpRequestMetadata(getThreadLocalRequest()));
                for (Object param : rpcRequest.getParameters())
                {
                    sb.append("  arg: ");
                    LoggingUtils.dumpObject(sb, param);
                }
            }
            logger.debug(sb.toString());
        }
        super.onAfterRequestDeserialized(rpcRequest);
    }

    @Override
    protected void onAfterResponseSerialized (String response)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Exiting GWT-RPC. Response: " + response);
        }

        super.onAfterResponseSerialized(response);
    }

    /**
     * Given the serialized request data for an RPC, this routine constructs the
     * RPC name (RemoteServiceInterface.method).
     * <p/>
     * This is sensitive to the actual _internal_ implementation of GWT and must
     * be updated if the GWT version changes. See {@link RepeatingCsrfSafeRpcBuilder#getRpcName(java.lang.String)}
     * for the client side implementation.
     */
    protected String getRpcName (String serializedRequest)
    {
        String[] parts = serializedRequest.split("\\|",8);
        return parts[5]+"."+parts[6];
    }
}
