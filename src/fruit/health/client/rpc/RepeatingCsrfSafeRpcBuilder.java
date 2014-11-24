package fruit.health.client.rpc;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import fruit.health.client.util.TimedEvent;
import fruit.health.client.util.Timer;
import fruit.health.shared.util.SharedConstants;

/**
 * We want most of our GWT-RPC calls to have the following characteristics:
 * <ol>
 * <li>CSRF safe</li>
 * <li>Restart after logging us back in if the server restarted and lost our
 * login session</li>
 * <li>Do all of this transparently to the calling client code and to the called
 * server code</li>
 * </ol>
 * This class helps us do all of the above.
 */
public class RepeatingCsrfSafeRpcBuilder extends RpcRequestBuilder
{

    private static final Logger logger = Logger.getLogger(RepeatingCsrfSafeRpcBuilder.class.getName());

    private static final RepeatingCsrfSafeRpcBuilder REPEATING_CSRF_SAFE_RPC_BUILDER = new RepeatingCsrfSafeRpcBuilder();

    private static String visitId = null;

    private RepeatingCsrfSafeRpcBuilder ()
    { }

    public static void setVisitId (String visitId)
    {
        assert (null != visitId);
        RepeatingCsrfSafeRpcBuilder.visitId = visitId;
    }

    public class RepeatingCallback implements RequestCallback
    {

        private final RepeatingRequestBuilder requestBuilder;
        private final RequestCallback realCallback;
        private String rpcName;

        public RepeatingCallback (RepeatingRequestBuilder rb, RequestCallback rc)
        {
            this.requestBuilder = rb;
            this.realCallback = rc;
        }
        
        public void setRpcName(String rpcName)
        {
            this.rpcName = rpcName;
        }

        @Override
        public void onResponseReceived (final Request request,
                final Response response)
        {
            requestBuilder.getTimer().end();

            if (Response.SC_OK != response.getStatusCode())
            {
                logger.log(Level.INFO, "RPC: " + rpcName + " Got " + response.getStatusCode() + ": " + response.getStatusText());
            }
            else
            {
                logger.log(Level.INFO, "RPC: " + rpcName + " Got success");
            }

            realCallback.onResponseReceived(request, response);
        }

        @Override
        public void onError (Request request, Throwable exception)
        {
            requestBuilder.getTimer().cancel();
            logger.log(Level.WARNING, "RPC: " + rpcName + " failed with " + exception.getClass().getName(), exception);
            realCallback.onError(request, exception);
        }
    }

    public class RepeatingRequestBuilder extends RequestBuilder
    {

        public RepeatingRequestBuilder (RequestBuilder rb)
        {
            super(rb.getHTTPMethod(), rb.getUrl());
        }

        private String requestData;
        private String rpcName = null;
        private Timer timer;

        public void reSend (RequestCallback rc) throws RequestException
        {
            timer.restart();
            super.sendRequest(requestData, rc);
        }

        @Override
        public Request sendRequest (String rd, RequestCallback rc)
                throws RequestException
        {
            requestData = rd;
            ((RepeatingCallback)rc).setRpcName(rpcName);
            return super.sendRequest(rd, rc);
        }

        @Override
        public void setRequestData (String s)
        {
            requestData = s;
            super.setRequestData(s);

            this.rpcName = getRpcName(s);
            this.timer = new Timer(TimedEvent.RPC_CALL, rpcName);
        }

        /**
         * Given the serialized request data for an RPC, this routine constructs
         * the RPC name (RemoteServiceInterface.method).
         * <p/>
         * This is done in javascript because JS and GWT Java split functions
         * behave differently.
         * <p/>
         * This is sensitive to the actual _internal_ implementation of GWT and
         * must be updated if the GWT version changes. See
         * {@link CsrfSafeRemoteServiceServlet#getRpcName(java.lang.String)} for
         * the server side implementation.
         */
        private native String getRpcName (String requestData)
        /*-{
            parts = requestData.split("|", 7);
            return parts[5]+"."+parts[6];
        }-*/;

        public Timer getTimer ()
        {
            return timer;
        }
    }

    @Override
    public RequestBuilder doCreate (String s)
    {
        return new RepeatingRequestBuilder(super.doCreate(s));
    }

    @Override
    public void doFinish (RequestBuilder rb)
    {
        String sessionId = Cookies.getCookie(SharedConstants.SESSION_ID_COOKIE_NAME);
        if (null!=sessionId)
        {
            rb.setHeader(SharedConstants.CSRF_GUARD_HEADER, sessionId);
        }
        
        if (null!=visitId)
        {
            rb.setHeader(SharedConstants.VISIT_ID_HEADER, visitId);
        }
        
        super.doFinish(rb);
    }

    @Override
    public void doSetCallback (RequestBuilder rb, RequestCallback rc)
    {
        super.doSetCallback(rb,
                new RepeatingCallback((RepeatingRequestBuilder)rb, rc));
    }

    /**
     * Given a basic GWT-RPC proxy service, enhance it to have the qualities of
     * {@link RepeatingCsrfSafeRpcBuilder}
     *
     * @param service
     *            A GWT-RPC proxy. Usually, acquired through
     *            {@link GWT#create(Class)}
     * @return An enhanced GWT-RPC proxy
     */
    public static ServiceDefTarget createService (ServiceDefTarget service)
    {
        service.setRpcRequestBuilder(REPEATING_CSRF_SAFE_RPC_BUILDER);
        return service;
    }
}
