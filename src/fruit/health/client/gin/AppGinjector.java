package fruit.health.client.gin;

import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;

import fruit.health.client.GlobalsHolder;
import fruit.health.client.I18NConstants;
import fruit.health.client.LocaleChooser;
import fruit.health.client.LoginStateManager;
import fruit.health.client.google.GoogleAnalytics;
import fruit.health.client.rpc.InitServiceAsync;
import fruit.health.client.util.URLCreator;
import fruit.health.client.view.ViewMaster;

@GinModules(AppGinModule.class)
public interface AppGinjector extends Ginjector {
	public interface PlaceControllerFactory {
		PlaceController get(EventBus eventBus);
	}
	
	// Globals
	GlobalsHolder getGlobalsHolder();
	
	// MVP activities and places
	ActivityMapper getActivityMapper();
	PlaceHistoryMapper getPlaceHistoryMapper();
	
	// MVP abstractions
	ViewMaster getViewMaster();

	// Services
    InitServiceAsync getInitService();
	
	// Other stuff
	EventBus getEventBus();
	LocaleChooser getLocaleChooser();
	
	// GWT Stuff, pulled out in order to aid testing
	Historian getPlaceHistoryHistorian();
	PlaceController getPlaceController();
	PlaceHistoryHandler getPlaceHistoryHandler();

	// App level stuff
	LoginStateManager getLoginStateManager();
    GoogleAnalytics getGoogleAnalytics();
    I18NConstants getI18NConstants();
    URLCreator getURLCreator();
}
