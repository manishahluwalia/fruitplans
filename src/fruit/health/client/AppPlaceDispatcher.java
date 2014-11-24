package fruit.health.client;

import java.util.logging.Logger;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;

import fruit.health.client.gin.AppGinjector;
import fruit.health.client.google.GoogleAnalytics;
import fruit.health.client.logging.ClientFlowEvent;
import fruit.health.client.logging.ClientFlowLogger;
import fruit.health.client.mvp.AuthenticatedPlace;
import fruit.health.client.mvp.BasePlace;
import fruit.health.client.mvp.LoginFlowPlace;
import fruit.health.client.places.enterPlan;
import fruit.health.shared.util.InlineMap;

public class AppPlaceDispatcher extends PlaceController
{
    private static final Logger logger = Logger.getLogger(AppPlaceDispatcher.class.getName());

    private LoginStateManager loginStateManager;
    private final GoogleAnalytics   googleAnalytics;

    public AppPlaceDispatcher (AppGinjector injector)
    {
        super(injector.getEventBus());

        googleAnalytics   = injector.getGoogleAnalytics();
    }

    public void setLoginStateManager(LoginStateManager loginStateManager)
    {
        this.loginStateManager = loginStateManager;
    }
    
    @Override
    public void goTo (Place p)
    {
        if (null == p)
        {
            logger.fine("target place in null; going home instead");
            p = new enterPlan(null);
        }

        BasePlace place = (BasePlace)p;

        final String placeDesc = place.toFullString();
        logger.fine("going to place: " + placeDesc);

        ClientFlowLogger.log(ClientFlowEvent.GOING_TO_PLACE, new InlineMap() {{
            _("placeName", placeDesc);
        }});

        // For some reason, merely sending us somewhere with the place
        // controller does not fire the history value change
        // handler. So, we do it ourselves. Moreover, this has the advantage (?)
        // that we can track where the user asked to be
        // sent, not where we eventually sent him.
        googleAnalytics.track(placeDesc);

        if (!loginStateManager.isLoggedIn() && place instanceof AuthenticatedPlace)
        {
            logger.severe("GOT AN AuthenticatedPlace. Not expecting one");
            throw new RuntimeException("Got an AuthenticatedPlace");
            
            /*
            logger.fine("not authenticated; saving place: " + place);

            // We are not logged in, but are trying to go somewhere that needs
            // authentication.  Make a note of the final destination and redirect
            // user to the login page instead
            place = new login(place, null, null);

            final String redirectedPlaceDesc = place.toFullString();
            ClientFlowLogger.log(ClientFlowEvent.REDIRECTED_TO_PLACE, new InlineMap() {{
                _("placeName", redirectedPlaceDesc);
            }});
            */
        }
        else if (loginStateManager.isLoggedIn() && place instanceof LoginFlowPlace)
        {
            /* This happens when a user logs in due to explicit user intent to do so.
             * e.g. the user clicked signup.
             * No 'afterLoginPlace' is specified, which just results in us trying
             * to reload the current place, which is, e.g. the signup place.
             * Redirect to the default place instead.
             */
            place = new enterPlan(null);

            final String redirectedPlaceDesc = place.toFullString();
            ClientFlowLogger.log(ClientFlowEvent.REDIRECTED_TO_PLACE, new InlineMap() {{
                _("placeName", redirectedPlaceDesc);
            }});
        }

        super.goTo(place);
    }
}
