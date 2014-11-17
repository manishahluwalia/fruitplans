package fruit.health.client.activities;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import fruit.health.client.entities.PlanData;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BaseActivity;
import fruit.health.client.places.comparePlans;
import fruit.health.client.places.enterPlan;
import fruit.health.client.places.home;
import fruit.health.client.view.HomeView;
import fruit.health.client.view.HomeView.Presenter;
import fruit.health.shared.util.RunnableWithArg;

public class HomeActivity extends BaseActivity<HomeView, Presenter> implements Presenter
{
    protected static final Logger logger = Logger.getLogger(HomeActivity.class.getName());
    private final List<PlanData> plans;

    public HomeActivity (home place, AppGinjector injector)
    {
        super(injector);
        this.plans  = injector.getGlobalsHolder().getPlans();
    }

    @Override
    protected void createView (RunnableWithArg<HomeView> callback)
    {
        viewMaster.getHomeView(callback);
    }

    @Override
    public void start (AcceptsOneWidget panel)
    {
        super.start(panel);
        view.showPlans(plans);
    }

    @Override
    public void enterDataClicked()
    {
        loginStateManager.goTo(new enterPlan(null));
    }

    @Override
    public void comparePlansClicked()
    {
        loginStateManager.goTo(new comparePlans());
    }

    @Override
    public void onPlanClicked(PlanData p)
    {
        loginStateManager.goTo(new enterPlan(p));
    }
}
