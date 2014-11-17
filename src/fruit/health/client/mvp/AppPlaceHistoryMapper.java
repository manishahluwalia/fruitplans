package fruit.health.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

import fruit.health.client.places.enterPlan;
import fruit.health.client.places.home;

@WithTokenizers( {
    home.Tokenizer.class,
    enterPlan.Tokenizer.class
	})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
