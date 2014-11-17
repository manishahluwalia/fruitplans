
package fruit.health.client.places;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;

import fruit.health.client.activities.EnterPlanActivity;
import fruit.health.client.entities.PlanData;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BasePlace;
import fruit.health.client.mvp.BasePlaceTokenizer;
import fruit.health.client.mvp.ReloadingPlace;

public class enterPlan extends BasePlace implements ReloadingPlace
{
    private final PlanData plan;
    
    public enterPlan (PlanData plan)
    {
        this.plan = plan;
    }

    public static class Tokenizer extends BasePlaceTokenizer<enterPlan>
            implements PlaceTokenizer<enterPlan>
    {
        @Override
        public enterPlan getBasePlace (String token)
        {
            return new enterPlan(new PlanData()); // XXX tokenize
        }
    }

    @Override
    public Activity getActivity (AppGinjector injector)
    {
        return new EnterPlanActivity(this, injector);
    }

    @Override
    public String getPlaceName ()
    {
        return constants.getEnterPlanPlaceTitle();
    }

    public PlanData getPlan()
    {
        return plan;
    }
}
