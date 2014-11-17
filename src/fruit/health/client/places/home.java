
package fruit.health.client.places;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;

import fruit.health.client.activities.HomeActivity;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BasePlace;
import fruit.health.client.mvp.BasePlaceTokenizer;

public class home extends BasePlace
{
    public home ()
    {
    }

    public static class Tokenizer extends BasePlaceTokenizer<home>
            implements PlaceTokenizer<home>
    {
        @Override
        public home getBasePlace (String token)
        {
            return new home();
        }
    }

    @Override
    public Activity getActivity (AppGinjector injector)
    {
        return new HomeActivity(this, injector);
    }

    @Override
    public String getPlaceName ()
    {
        return constants.getHomePlaceTitle();
    }
}
