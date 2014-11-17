
package fruit.health.client.places;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;

import fruit.health.client.activities.ComparePlanActivity;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BasePlace;
import fruit.health.client.mvp.BasePlaceTokenizer;

public class comparePlans extends BasePlace
{
    public comparePlans ()
    {
    }

    public static class Tokenizer extends BasePlaceTokenizer<comparePlans>
            implements PlaceTokenizer<comparePlans>
    {
        @Override
        public comparePlans getBasePlace (String token)
        {
            return new comparePlans();
        }
    }

    @Override
    public Activity getActivity (AppGinjector injector)
    {
        return new ComparePlanActivity(this, injector);
    }

    @Override
    public String getPlaceName ()
    {
        return constants.getComparePlansPlaceTitle();
    }
}
