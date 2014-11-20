
package fruit.health.client.places;

import java.util.List;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;

import fruit.health.client.activities.CompareActivity;
import fruit.health.client.entities.PlanData;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BasePlace;
import fruit.health.client.mvp.BasePlaceTokenizer;

public class compare extends BasePlace
{
    private final List<PlanData> plans;

    public compare (List<PlanData> plans)
    {
        this.plans = plans;
    }

    public static class Tokenizer extends BasePlaceTokenizer<compare>
            implements PlaceTokenizer<compare>
    {
        @Override
        public compare getBasePlace (String token)
        {
            return new compare(null); // XXX tokenize
        }
    }

    @Override
    public Activity getActivity (AppGinjector injector)
    {
        return new CompareActivity(this, injector);
    }

    @Override
    public String getPlaceName ()
    {
        return constants.getComparePlaceTitle();
    }

    public List<PlanData> getPlans () {
        return plans;
    }
}
