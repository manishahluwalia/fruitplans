package fruit.health.client.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.RunAsyncCallback;

public abstract class FailureLoggingAsyncCallback implements RunAsyncCallback
{
    private final static Logger logger = Logger.getLogger(FailureLoggingAsyncCallback.class.getName());
    
    private final StackTraceElement[] realStackTrace;

    public FailureLoggingAsyncCallback()
    {
        realStackTrace = new Exception().getStackTrace();
    }
    
    @Override
    public void onFailure (Throwable caught)
    {
        // Remove the FailureLoggingAsyncCallback constructor and one more entry, which we expect to be the anonymous constructor
        StackTraceElement[] fakedStackTrace = new StackTraceElement[realStackTrace.length-2];
        System.arraycopy(realStackTrace, realStackTrace.length-fakedStackTrace.length, fakedStackTrace, 0, fakedStackTrace.length);
        caught.setStackTrace(fakedStackTrace);
        
        logger.log(Level.SEVERE, "GWT.runAsync call failed with " + caught.getClass().getName(), caught);
    }
}
