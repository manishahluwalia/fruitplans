package fruit.health.client;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;

import fruit.health.client.gin.AppGinjector;
import fruit.health.client.google.GoogleAnalytics;
import fruit.health.client.logging.ClientFlowEvent;
import fruit.health.client.logging.ClientFlowLogger;
import fruit.health.client.mvp.BasePlace;
import fruit.health.client.mvp.ReloadingPlace;
import fruit.health.client.places.enterPlan;
import fruit.health.shared.util.InlineMap;

@Singleton
public class LoginStateManager {
    public static final Logger logger = Logger.getLogger(LoginStateManager.class.getName());
    
    @Singleton
    public static class DefaultPlaceFactory
    {
        public BasePlace makePlace ()
        {
            return new enterPlan(null);
        }
    }

    private final GoogleAnalytics googleAnalytics;
	
	private final PlaceController placeController;

	/**
	 * Instead of storing an instance of the default place, we use a factory to
	 * create instances. This way, the default place could be a {@link ReloadingPlace},
	 * for instance.
	 */
	private final DefaultPlaceFactory defaultPlaceFactory;
	
	@Inject
	public LoginStateManager(DefaultPlaceFactory defaultPlaceFactory, AppGinjector injector) {
		this.defaultPlaceFactory = defaultPlaceFactory;
		this.placeController = injector.getPlaceController();
		this.googleAnalytics = injector.getGoogleAnalytics();
	}

    /** 
     * Reload the current place where we are at. This will be a no-op if we aren't
     * at a {@link ReloadingPlace}
     */
    public void reloadCurrentPlace ()
    {   
        goTo((BasePlace)placeController.getWhere());
    }   
	

    public BasePlace getDefaultPlace ()
    {
        return defaultPlaceFactory.makePlace();
    }

    /**
     * Go to the given {@link Place}.
     * <p/>
     * There may be some constraints that do not allow us to (immediately) go
     * where directed. E.g., the user may be trying to go to a place / perform
     * an action that requires the user to be logged in. In such a case, we will
     * make a note of the desired final destination and redirect the user to the
     * login page. From the login page, the user may need to go to the signup
     * page (if the user doesn't have an account, etc.). This is called the
     * "login flow". After the login flow is successfully completed, the user is
     * sent where she wanted to go.
     * <p/>
     * An intentional move by the user to visit a different place (e.g. company
     * info) terminates the above mentioned login flow.
     * 
     * @param place The desired target.  if null, goes to the default place
     */
	public void goTo(BasePlace place)
	{
        if (null == place)
        {
            place = getDefaultPlace();
        }

        logger.fine("LoginStateManager.goTo: " + place);
        
        final String placeDesc = place.toFullString();
        
        ClientFlowLogger.log(ClientFlowEvent.GOING_TO_PLACE, new InlineMap() {{
            _("placeName",placeDesc);
        }});
        
        // For some reason, merely sending us somewhere with the place
        // controller does not fire the history value change
        // handler. So, we do it ourselves. Moreover, this has the advantage (?)
        // that we can track where the user asked to be
        // sent, not where we eventually sent him.
        googleAnalytics.track(placeDesc);

        // No special cases apply
        placeController.goTo(place);
	}
}
