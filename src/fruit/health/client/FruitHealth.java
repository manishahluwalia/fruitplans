package fruit.health.client;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import logging.client.RemoteLogHandler;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

import fruit.health.client.entities.PlanData;
import fruit.health.client.events.PlanAddedEvent;
import fruit.health.client.events.PlanAddedEvent.PlanAddedEventHandler;
import fruit.health.client.events.PlanEditDoneEvent;
import fruit.health.client.events.PlanEditDoneEvent.PlanEditDoneEventHandler;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.logging.ClientFlowEvent;
import fruit.health.client.logging.ClientFlowLogger;
import fruit.health.client.mvp.BasePlace;
import fruit.health.client.places.compare;
import fruit.health.client.places.enterPlan;
import fruit.health.client.rpc.RepeatingCsrfSafeRpcBuilder;
import fruit.health.client.util.ConvergenceWaiter;
import fruit.health.client.util.FailureNotifyingCallback;
import fruit.health.client.util.JsLoader;
import fruit.health.client.util.TimedEvent;
import fruit.health.client.util.Timer;
import fruit.health.client.view.ViewMaster;
import fruit.health.client.view.ViewMaster.Presenter;
import fruit.health.shared.dto.InitInfo;
import fruit.health.shared.util.InlineMap;
import fruit.health.shared.util.RunnableWithArg;
import fruit.health.shared.util.SharedConstants;

class _UncaughtExceptionHandler implements
        com.google.gwt.core.client.GWT.UncaughtExceptionHandler
{
    private static final Logger logger = Logger.getLogger(_UncaughtExceptionHandler.class.getName());

    private final ViewMaster    _viewMaster;
    private final I18NConstants _constants;

    public _UncaughtExceptionHandler (ViewMaster viewMaster, I18NConstants constants)
    {
        _viewMaster = viewMaster;
        _constants = constants;
    }

    @Override
    public void onUncaughtException (Throwable e)
    {
        logger.log(Level.SEVERE, "Uncaught exception " + e.getClass().getName() + " escaped: ", e);
        _viewMaster.alertDialog(
                _constants.getInternalErrorDialogTitle(),
                _constants.getInternalErrorDialogMessage(),
                null, null);
    }
}

/**
 * Entry point class
 */
public class FruitHealth implements EntryPoint, Presenter
{
    private static final int gwtStartTime = Timer.getTime();

    private final static Logger logger = Logger.getLogger(FruitHealth.class.getName());
    
    private PlaceController placeController;
    
    @Override
    public void onModuleLoad ()
    {
        final int gwtModuleStartTime = Timer.getTime();
        final int pageStartTime = (int) ( Long.parseLong(getPageStartTime()) % SharedConstants.TIME_MODULO_MS );
        final int jsStartTime = (int) ( Long.parseLong(getJsStartTime()) % SharedConstants.TIME_MODULO_MS );
        
        // create logger (messages are queued until we finish initialization below)
        RemoteLogHandler.get().initialize();
        logger.log(Level.FINER, "starting...");

        final AppGinjector injector = GWT.create(AppGinjector.class);
        final EventBus eventBus = injector.getEventBus();
        final ViewMaster viewMaster = injector.getViewMaster();
        final I18NConstants constants = injector.getI18NConstants();
        placeController = injector.getPlaceController();
        
        FailureNotifyingCallback.initialize(injector);
        BasePlace.initialize(injector);
        
        final ConvergenceWaiter appStart = new ConvergenceWaiter(2) {
            @Override
            public void onFailure ()
            {
                logger.severe("Failure loading app. Will reload");
                viewMaster.alertDialog(constants.getModuleLoadingErrorTitle(),
                        constants.getModuleLoadingErrorBody(),
                        new RunnableWithArg<Void>() {
                    @Override
                    public void run (Void param)
                    {
                        Window.Location.reload();
                    }
                }, null);
            }

            @Override
            public void onAllSuccess ()
            {
                // Log startup times
                int scriptStartingDone = Timer.getTime();
                Timer.log(TimedEvent.PAGE_START_TO_JS_START, pageStartTime, jsStartTime);
                Timer.log(TimedEvent.PAGE_START_TO_GWT_START, pageStartTime, gwtStartTime);
                Timer.log(TimedEvent.PAGE_START_TO_MODULE_START, pageStartTime, gwtModuleStartTime);
                Timer.log(TimedEvent.PAGE_START_TO_MODULE_LOAD_END, pageStartTime, scriptStartingDone);

                PlaceHistoryHandler historyHandler = injector.getPlaceHistoryHandler();

                historyHandler.register(injector.getPlaceController(), eventBus, new enterPlan(null));
                historyHandler.handleCurrentHistory();
                int historyHandlingDone = Timer.getTime();

                /* Log timers */
                Timer.log(TimedEvent.PAGE_START_TO_HISTORY_HANDLING, pageStartTime, historyHandlingDone);
            }
        };

        String serverLogging = Location.getParameter("serverLogging");
        injector.getInitService().initClient(Document.get().getReferrer(),
                serverLogging, new AsyncCallback<InitInfo>() {
            @Override
            public void onSuccess (InitInfo result)
            {
                int initialLoginStateGatheringDone = Timer.getTime();
                Timer.log(TimedEvent.PAGE_START_TO_GATHERING_INITIAL_LOGIN_RELATED_STATE, pageStartTime, initialLoginStateGatheringDone );

                onModuleInitialized(result, appStart);
            }

            @Override
            public void onFailure (Throwable caught)
            {
                logger.log(Level.SEVERE, "init service failed", caught);
                appStart.waiteeFinished(false);
            }
        });

        // Log the fact that the app was loaded. Set up handlers to log when
        // the app is unloaded or a history change happens.
        ClientFlowLogger.log(ClientFlowEvent.APP_LOADED, new InlineMap() {
            {
                Document doc = Document.get();
                _("referrer", doc.getReferrer());
                _("url", doc.getURL());
                _("width", doc.getClientWidth());
                _("height", doc.getClientHeight());
                _("user-agent", Window.Navigator.getUserAgent());
                _("cookies-enabled", Window.Navigator.isCookieEnabled());
            }
        });

        Window.addWindowClosingHandler(new ClosingHandler() {
            @Override
            public void onWindowClosing (ClosingEvent e)
            {
                ClientFlowLogger.log(ClientFlowEvent.APP_UNLOADED);
            }
        });

        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange (final ValueChangeEvent<String> e)
            {
                ClientFlowLogger.log(ClientFlowEvent.HISTORY_CHANGED,
                        new InlineMap() {
                            {
                                _("token", e.getValue());
                            }
                        });
            }
        });

        // Set a window title. For now, this is just the prefix
        Window.setTitle(constants.getWindowTitlePrefix());

        AcceptsOneWidget mainBody = viewMaster.initialize(this, injector);

        // This will be cleared at the completion of the first activity load
        viewMaster.showBusy(true);

        GWT.setUncaughtExceptionHandler(new _UncaughtExceptionHandler(viewMaster,
                constants));

        // Start ActivityManager for the main widget with our ActivityMapper
        ActivityManager activityManager =
                new ActivityManager(injector.getActivityMapper(), eventBus);
        activityManager.setDisplay(mainBody);

        new com.google.gwt.user.client.Timer() {
            @Override
            public void run ()
            {
                new JsLoader("google-analytics", new String[] {"http://www.google-analytics.com/ga.js"}, true).load();
            }
        }.schedule(5);

        eventBus.addHandler(PlanAddedEvent.TYPE, new PlanAddedEventHandler()
        {
            @Override
            public void onPlanAdded(PlanData plan)
            {
                injector.getGlobalsHolder().addPlan(plan);
            }
        });
        eventBus.addHandler(PlanEditDoneEvent.TYPE, new PlanEditDoneEventHandler()
        {
            @Override
            public void onCancel()
            {
                List<PlanData> plans = injector.getGlobalsHolder().getPlans();
                if (plans.size()>0) {
                    injector.getLoginStateManager().goTo(new compare(plans));
                } else {
                    injector.getLoginStateManager().goTo(new enterPlan(null)); // XXX should go to _static_ home page
                }
            }
        });
        
        logger.log(Level.FINER, "Everything initialized");

        appStart.waiteeFinished(true);
    }

    protected void onModuleInitialized (InitInfo initInfo, ConvergenceWaiter appStart)
    {
        // allow accumulated logs (and all future logs) to be sent to the server
        RemoteLogHandler remoteLogHandler = RemoteLogHandler.get();
        HashMap<String, String> logContext = remoteLogHandler.getContext();
        logContext.put("ver", initInfo.getCommitId());
        logContext.put("visitId", initInfo.getVisitId());

        String browserCookie = Cookies.getCookie(SharedConstants.BROWSER_COOKIE_NAME);
        if (null == browserCookie)
        {
            logger.warning("No bcookie");
        }
        else
        {
            logContext.put("browser", browserCookie);
        }
        remoteLogHandler.contextInitalized();

        setGoogleAnalyticsId(initInfo.getGoogleAnalyticsId());
        RepeatingCsrfSafeRpcBuilder.setVisitId(initInfo.getVisitId());

        appStart.waiteeFinished(true);
    }
    

    private native void setGoogleAnalyticsId (String googleAnalyticsId)
    /*-{
      var _gaq = $wnd._gaq || [];
      _gaq.push(['_setAccount', googleAnalyticsId]);
    }-*/;

    
    private native String getJsStartTime()
    /*-{
        return $wnd.jsStartTime.toString();
    }-*/;
    
    private native String getPageStartTime()
    /*-{
        return $wnd.pageStartTime.toString();
    }-*/;
}
