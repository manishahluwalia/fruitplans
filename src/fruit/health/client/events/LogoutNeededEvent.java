
package fruit.health.client.events;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import fruit.health.client.events.LogoutNeededEvent.LogoutNeededEventHandler;


public class LogoutNeededEvent extends GwtEvent<LogoutNeededEventHandler>
{
    public static interface LogoutNeededEventHandler extends EventHandler
    {
        void onLogout ();
    }

    public static Type<LogoutNeededEventHandler> TYPE = new Type<LogoutNeededEventHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<LogoutNeededEventHandler> getAssociatedType ()
    {
        return TYPE;
    }

    @Override
    protected void dispatch (LogoutNeededEventHandler handler)
    {
        handler.onLogout();
    }

}
