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
import fruit.health.client.places.home;
import fruit.health.client.rpc.RepeatingCsrfSafeRpcBuilder;
import fruit.health.client.util.ConvergenceWaiter;
import fruit.health.client.util.FailureNotifyingCallback;
import fruit.health.client.util.JsLoader;
import fruit.health.client.util.TimedEvent;
import fruit.health.client.util.Timer;
import fruit.health.client.view.ViewMaster;
import fruit.health.client.view.ViewMaster.Presenter;
import fruit.health.shared.dto.InitInfo;
import fruit.health.shared.dto.LoginInfo;
import fruit.health.shared.util.InlineMap;
import fruit.health.shared.util.Pair;
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
        RepeatingCsrfSafeRpcBuilder.setI18NConstants(injector.getI18NConstants());
        RepeatingCsrfSafeRpcBuilder.setViewMaster(viewMaster);
        
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
                Timer.log(TimedEvent.JS_START_TO_GWT_START, jsStartTime, gwtStartTime);
                Timer.log(TimedEvent.JS_START_TO_MODULE_START, jsStartTime, gwtModuleStartTime);
                Timer.log(TimedEvent.JS_START_TO_MODULE_LOAD_END, jsStartTime, scriptStartingDone);

                PlaceHistoryHandler historyHandler = injector.getPlaceHistoryHandler();

                historyHandler.register(injector.getPlaceController(), eventBus, new home());
                historyHandler.handleCurrentHistory();
                int historyHandlingDone = Timer.getTime();

                /* Log timers */
                Timer.log(TimedEvent.JS_START_TO_HISTORY_HANDLING, jsStartTime, historyHandlingDone);
            }
        };

        String serverLogging = Location.getParameter("serverLogging");
        injector.getInitService().initClient(Document.get().getReferrer(),
                serverLogging, new AsyncCallback<Pair<InitInfo,LoginInfo>>() {
            @Override
            public void onSuccess (Pair<InitInfo, LoginInfo> result)
            {
                int initialLoginStateGatheringDone = Timer.getTime();
                Timer.log(TimedEvent.JS_START_TO_GATHERING_INITIAL_LOGIN_RELATED_STATE, jsStartTime, initialLoginStateGatheringDone );

                injector.getLoginStateManager().setLoginInfo(result.getB());
                onModuleInitialized(result.getA(), appStart);
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
                    injector.getLoginStateManager().goTo(new home());
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

    
    
    @Override
    public void onLogoClicked()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLogoutClicked()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onProfileClicked()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLoginClicked()
    {
        // placeController.goTo(new login(null, null, null));
    }

    @Override
    public void onSignupClicked()
    {
        // placeController.goTo(new signup(null));
    }

    @Override
    public void onForgotPasswordClicked()
    {
        // TODO Auto-generated method stub
        
    }
}
