package fruit.health.client;

import java.util.logging.Logger;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;

import fruit.health.client.gin.AppGinjector;
import fruit.health.client.google.GoogleAnalytics;
import fruit.health.client.logging.ClientFlowEvent;
import fruit.health.client.logging.ClientFlowLogger;
import fruit.health.client.mvp.BasePlace;
import fruit.health.client.places.enterPlan;
import fruit.health.shared.util.InlineMap;

public class AppPlaceDispatcher extends PlaceController
{
    private static final Logger logger = Logger.getLogger(AppPlaceDispatcher.class.getName());

    private final GoogleAnalytics   googleAnalytics;

    public AppPlaceDispatcher (AppGinjector injector)
    {
        super(injector.getEventBus());

        googleAnalytics   = injector.getGoogleAnalytics();
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

        super.goTo(place);
    }
}
