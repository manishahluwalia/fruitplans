package fruit.health.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.Event;

import fruit.health.client.events.PlanEditDoneEvent.PlanEditDoneEventHandler;

public class PlanEditDoneEvent extends Event<PlanEditDoneEventHandler>
{
    public static interface PlanEditDoneEventHandler extends EventHandler
    {
        void onCancel();
    }

    public static Type<PlanEditDoneEventHandler> TYPE = new Type<PlanEditDoneEventHandler>();

    @Override
    public com.google.web.bindery.event.shared.Event.Type<PlanEditDoneEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(PlanEditDoneEventHandler handler)
    {
       handler.onCancel();
    }

}
