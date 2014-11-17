package fruit.health.client.view.desktop;

import com.google.gwt.user.client.ui.Composite;

public abstract class BaseViewImpl<P> extends Composite
{
    protected static DesktopBrowserViewMaster viewMaster;
    
    protected P presenter;
    
    static void setViewMaster(DesktopBrowserViewMaster viewMaster)
    {
        BaseViewImpl.viewMaster = viewMaster;
    }
    
    public void setPresenter(P presenter)
    {
        this.presenter = presenter;
    }
}
