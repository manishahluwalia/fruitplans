package fruit.health.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.Event;

import fruit.health.client.entities.PlanData;
import fruit.health.client.events.PlanAddedEvent.PlanAddedEventHandler;

public class PlanAddedEvent extends Event<PlanAddedEventHandler>
{
    public static interface PlanAddedEventHandler extends EventHandler
    {
        void onPlanAdded(PlanData plan);
    }

    public static Type<PlanAddedEventHandler> TYPE = new Type<PlanAddedEventHandler>();

    @Override
    public com.google.web.bindery.event.shared.Event.Type<PlanAddedEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    private final PlanData plan;
    
    public PlanAddedEvent(PlanData plan)
    {
        this.plan = plan;
    }

    @Override
    protected void dispatch(PlanAddedEventHandler handler)
    {
       handler.onPlanAdded(plan);
    }

}
