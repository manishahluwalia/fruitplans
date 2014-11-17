package fruit.health.client.util;


public abstract class BusyHandlingCallback<T> extends FailureNotifyingCallback<T>
{
    private final boolean showingBusy;
    
    public BusyHandlingCallback()
    {
        if (null!=viewMaster)
        {
            showingBusy = true;
            viewMaster.showBusy(true);
        }
        else
        {
            showingBusy = false;
        }
    }
    
    @Override
    public void onSuccess(T result)
    {
        if (showingBusy)
        {
            viewMaster.showBusy(false);
        }
    }

    @Override
    public void onFailure(Throwable caught)
    {
        super.onFailure(caught);
        if (showingBusy)
        {
            viewMaster.showBusy(false);
        }
    }
}
