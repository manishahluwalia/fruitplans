
package fruit.health.client.mvp;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

import fruit.health.client.I18NConstants;
import fruit.health.client.LoginStateManager;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.util.TimedEvent;
import fruit.health.client.util.Timer;
import fruit.health.client.view.ViewMaster;
import fruit.health.shared.util.RunnableWithArg;

public abstract class BaseActivity<V extends BaseView<P>, P> extends AbstractActivity
{
    private static final Logger logger = Logger.getLogger(BaseActivity.class.getName());

    protected final LoginStateManager loginStateManager;
    protected final PlaceController placeController;
    protected final I18NConstants constants;
    protected final ViewMaster viewMaster;

    // set in start()
    protected EventBus eventBus;
    protected V        view;

    private final Timer timer;
    private boolean activityStartupFinished = false;

    public BaseActivity (AppGinjector injector)
    {
        loginStateManager = injector.getLoginStateManager();
        placeController = injector.getPlaceController();
        constants = injector.getI18NConstants();
        viewMaster = injector.getViewMaster();

        logger.finer("Starting activity " + getClass().getName());
        this.timer = new Timer(TimedEvent.ACTIVITY_CREATION,
                getClass().getName());
    }

    /**
     * This routine is called when the activity is ready to start. At this
     * point, the view has been created, the eventBus has been set and the
     * busy-waiting icon is being displayed. The activity should not manipulate
     * the busy-waiting icon directly. The activity should end its processing by
     * either calling {@link #endStart(boolean)}, or {@link #deferredStartup()}.
     *
     * @param panel
     *            The panel as provided to
     *            {@link AbstractActivity#start(AcceptsOneWidget, com.google.gwt.event.shared.EventBus)}
     */
    protected void start (AcceptsOneWidget panel)
    {
        panel.setWidget(view.asWidget());
        endStart(true);
    }

    @Override
    public final void start (final AcceptsOneWidget panel,
            com.google.gwt.event.shared.EventBus eventBus)
    {
        this.eventBus = eventBus;
        viewMaster.showBusy(true);

        createView(new RunnableWithArg<V>() {
            @SuppressWarnings("unchecked")
            @Override
            public void run (V view)
            {
                BaseActivity.this.view = view;
                view.setPresenter((P) BaseActivity.this);
                start(panel);
            }
        });
    }

    /**
     * The deriving class needs to implement this method. It should create the
     * view needed by the activity and invoke the callback with it.
     */
    protected abstract void createView (RunnableWithArg<V> callback);

    /**
     * To be called by the activity when the entire activity has started and is
     * ready to interact with the user.
     *
     * @param succeeded
     *            true if the activity started successfully, false if it didn't.
     */
    public final void endStart (boolean succeeded)
    {
        /*
         * It is tempting to do a panel.setWidget(view.asWidget()) here, since
         * that's the last thing almost every activity does. However, some
         * activities must set the view in the panel before they do some other
         * stuff. To keep this flexibility for the derived classes, we don't do
         * that here and require all classed to do it on their own.
         */

        if (!activityStartupFinished)
        {
            activityStartupFinished = true;
            viewMaster.showBusy(false);
            timer.end();

            logger.fine("Activity " + getClass().getName()
                    + " started with success: " + succeeded);
        }
        else
        {
            logger.log(Level.WARNING,
                    "endStart/deferredStartup called multiple times",
                    new Exception());
            viewMaster.showBusy(false); // Hack! Need a cleaner mechanism
        }
    }

    /**
     * Certain activities will do some processing during startup, and will then
     * hand over control to another activity (e.g. via sending a message on the
     * event bus). Such activities should call deferredStartup() on their way
     * out, BEFORE sending such an event.
     */
    protected final void deferredStartup ()
    {
        if (!activityStartupFinished)
        {
            viewMaster.showBusy(false); // Just to keep showBusy(true) and
                                         // showBusy(false) in pairs.
                                         // showBusy(true) is going to be called
                                         // again by the next activity anyway
            timer.cancel();
            logger.fine("Activity " + getClass().getName()
                    + " deferred to another activity");
            activityStartupFinished = false;
        }
        else
        {
            // This is _clearly_ a bug on the part of the caller. Do the best we
            // can to handle it

            logger.log(Level.SEVERE,
                    "endStart/deferredStartup called multiple times",
                    new Exception());
            viewMaster.showBusy(false); // Hack! Need a cleaner mechanism in
                                         // case the caller did not really have
                                         // a matching call to showBusy(true)
        }
    }
}
