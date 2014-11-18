package fruit.health.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

import fruit.health.client.places.editScenario;
import fruit.health.client.places.enterPlan;
import fruit.health.client.places.home;

@WithTokenizers( {
    home.Tokenizer.class,
    enterPlan.Tokenizer.class,
    editScenario.Tokenizer.class
	})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
