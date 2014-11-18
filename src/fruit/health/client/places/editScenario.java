
package fruit.health.client.places;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;

import fruit.health.client.activities.EditScenarioActivity;
import fruit.health.client.entities.Scenario;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BasePlace;
import fruit.health.client.mvp.BasePlaceTokenizer;

public class editScenario extends BasePlace
{
    private final Scenario scenario;
    
    public editScenario (Scenario scenario)
    {
        this.scenario = scenario;
    }

    public static class Tokenizer extends BasePlaceTokenizer<editScenario>
            implements PlaceTokenizer<editScenario>
    {
        @Override
        public editScenario getBasePlace (String token)
        {
            return new editScenario(null); // XXX tokenize
        }
    }

    @Override
    public Activity getActivity (AppGinjector injector)
    {
        return new EditScenarioActivity(this, injector);
    }

    @Override
    public String getPlaceName ()
    {
        return constants.getEditScenarioPlaceTitle();
    }

    public Scenario getScenario()
    {
        return scenario;
    }
}
