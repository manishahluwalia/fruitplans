package fruit.health.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

import fruit.health.client.places.compare;
import fruit.health.client.places.enterPlan;

@WithTokenizers( {
    enterPlan.Tokenizer.class,
    compare.Tokenizer.class
	})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
