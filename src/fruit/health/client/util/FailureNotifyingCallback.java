package fruit.health.client.util;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fruit.health.client.I18NConstants;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.view.ViewMaster;

public abstract class FailureNotifyingCallback<T> implements AsyncCallback<T>
{
    protected static ViewMaster viewMaster = null;
    private static String errorDialogTitle;
    private static String errorDialogMessage;
    
    public static void initialize(AppGinjector injector)
    {
        viewMaster = injector.getViewMaster();
        I18NConstants constants = injector.getI18NConstants();
        errorDialogTitle = constants.getInternalErrorDialogTitle();
        errorDialogMessage = constants.getInternalErrorDialogMessage();
    }
    
    @Override
    public void onFailure(Throwable caught)
    {
        if (!handleFailure(caught))
        {
            if (null!=viewMaster)
            {
                viewMaster.alertDialog(errorDialogTitle, errorDialogMessage, null, null);
            }
        }
    }
    
    /**
     * When a failure happens, this routine is called. If it indicates that it handled the
     * failure (by returning true), then failure processing stops.
     * @param caught
     * @return true iff the failure was handled and should not be processed further
     */
    protected boolean handleFailure(Throwable caught)
    {
        return false;
    }

}
