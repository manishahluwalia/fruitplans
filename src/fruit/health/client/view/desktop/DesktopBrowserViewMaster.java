package fruit.health.client.view.desktop;

import javax.inject.Singleton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import fruit.health.client.LocaleChooser;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.util.FailureLoggingAsyncCallback;
import fruit.health.client.util.TimedEvent;
import fruit.health.client.util.Timer;
import fruit.health.client.view.EnterPlanView;
import fruit.health.client.view.HomeView;
import fruit.health.client.view.LoginView;
import fruit.health.client.view.SignupView;
import fruit.health.client.view.ViewMaster;
import fruit.health.client.view.desktop.resources.Resources;
import fruit.health.client.view.desktop.views.EnterPlanViewImpl;
import fruit.health.client.view.desktop.views.HomeViewImpl;
import fruit.health.client.view.desktop.views.LoginViewImpl;
import fruit.health.client.view.desktop.views.SignupViewImpl;
import fruit.health.shared.entities.User;
import fruit.health.shared.util.RunnableWithArg;

/**
 * The {@link ViewMaster} implementation for desktop browsers
 */
@Singleton
public class DesktopBrowserViewMaster implements ViewMaster
{
    private LoginView loginView;
    private SignupView signupView;
    private FullScreen screen;
    private HomeView homeView;
    private EnterPlanView enterPlanView;
    
    public DesktopBrowserViewMaster()
    {
        BaseViewImpl.setViewMaster(this);
    }
    
    private void getLazyLoadedViews(RunAsyncCallback callback)
    {
        GWT.runAsync(callback);
    }

    @Override
    public void getLoginView(final RunnableWithArg<LoginView> callback)
    {
        if (null == loginView)
        {
            final Timer timer = new Timer(TimedEvent.VIEW_CREATION, "DesktopBrowserViewMaster.loginView");
            loginView = new LoginViewImpl(this);
            timer.end();
        }

        callback.run(loginView);
    }

    @Override
    public void getSignupView(final RunnableWithArg<SignupView> callback)
    {
        if (null == signupView)
        {
            final Timer timer = new Timer(TimedEvent.VIEW_CREATION, "DesktopBrowserViewMaster.signupView");
            getLazyLoadedViews(new FailureLoggingAsyncCallback() {                
                @Override
                public void onSuccess ()
                {
                    signupView = new SignupViewImpl(DesktopBrowserViewMaster.this);
                    timer.end();
                    callback.run(signupView);
                }
            });
            return;
        }

        callback.run(signupView);
    }

    @Override
    public void getHomeView(RunnableWithArg<HomeView> callback)
    {
        if (null == homeView)
        {
            final Timer timer = new Timer(TimedEvent.VIEW_CREATION, "DesktopBrowserViewMaster.homeView");
            homeView = new HomeViewImpl(this);
            timer.end();
        }

        callback.run(homeView);
    }

    @Override
    public void getEnterPlanView(RunnableWithArg<EnterPlanView> callback)
    {
        if (null == enterPlanView)
        {
            final Timer timer = new Timer(TimedEvent.VIEW_CREATION, "DesktopBrowserViewMaster.enterPlanView");
            enterPlanView = new EnterPlanViewImpl(this);
            timer.end();
        }

        callback.run(enterPlanView);
    }

    /*
    @Override
    public void getUploadView(RunnableWithArg<UploadView> callback)
    {
        if (null == uploadView)
        {
            final Timer timer = new Timer(TimedEvent.VIEW_CREATION, "DesktopBrowserViewMaster.uploadView");
            uploadView = new UploadViewImpl();
            timer.end();
        }

        callback.run(uploadView);
    }

    @Override
    public void getViewTablesView(final RunnableWithArg<ViewTablesView> callback)
    {
        if (null == viewTablesView)
        {
            final Timer timer = new Timer(TimedEvent.VIEW_CREATION, "DesktopBrowserViewMaster.viewTablesView");
            getLazyLoadedViews(new FailureLoggingAsyncCallback() {                
                @Override
                public void onSuccess ()
                {
                    viewTablesView = new ViewTablesViewImpl();
                    timer.end();
                    callback.run(viewTablesView);
                }
            });
            return;
        }

        callback.run(viewTablesView);
    }

    @Override
    public void getViewOneTableView(final RunnableWithArg<ViewOneTableView> callback)
    {
        if (null == viewOneTableView)
        {
            final Timer timer = new Timer(TimedEvent.VIEW_CREATION, "DesktopBrowserViewMaster.viewOneTableView");
            getLazyLoadedViews(new FailureLoggingAsyncCallback() {                
                @Override
                public void onSuccess ()
                {
                    viewOneTableView = new ViewOneTableViewImpl();
                    timer.end();
                    callback.run(viewOneTableView);
                }
            });
            return;
        }

        callback.run(viewOneTableView);
    }
    */
    
    @Override
    public AcceptsOneWidget initialize (ViewMaster.Presenter presenter, AppGinjector injector)
    {
        Resources.INSTANCE.style().ensureInjected();
        screen = new FullScreen(presenter, injector);
        RootLayoutPanel.get().add(screen);
        return screen.getBodyPanel();
    }

    @Override
    public <T> void alertDialog (String title, String msg, RunnableWithArg<T> callback, T scope)
    {
        Window.alert(msg);
        if (null!=callback)
        {
            callback.run(scope);
        }
    }

    public <T> void confirmDialog (String title, String msg, ConfirmCallback<T> callback, T arg)
    {
        callback.onConfirm(Window.confirm(msg), arg);
    }

    @Override
    public void setViewName(String viewName)
    {
        screen.setViewName(viewName);
    }
    
    @Override
    public void showBusy (boolean busy)
    {
        screen.showBusy(busy);
    }
    
    @Override
    public void setLoggedInUser(User user)
    {
        screen.showLoggedInMenu(null!=user);
    }

    @Override
    public void setLocaleChooser(LocaleChooser localeChooser)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void showLoggedInMenu(boolean isAuthenticated)
    {
        screen.showLoggedInMenu(isAuthenticated);
    }
}
