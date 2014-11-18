package fruit.health.client.activities;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import fruit.health.client.entities.PlanData;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BaseActivity;
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
        view.prepareFor(plans.size());
        
        for (int i=0; i<plans.size(); i++) {
            PlanData p = plans.get(i);
            view.showPlan(p, i, p.planName, p.premium*12, expectToPay(p, 0), expectToPay(p, 2000), expectToPay(p, 25000));
        }
    }

    @Override
    public void enterDataClicked()
    {
        loginStateManager.goTo(new enterPlan(null));
    }

    @Override
    public void onPlanClicked(PlanData p)
    {
        loginStateManager.goTo(new enterPlan(p));
    }

    private int expectToPay(PlanData p, int medicalExpenses)
    {
        int expectedCosts = medicalExpenses;
        int expectToPay;
        if (expectedCosts < p.deductible) {
            expectToPay = expectedCosts;
        } else {
            expectToPay = p.deductible + ( expectedCosts - p.deductible ) * p.copay / 100;
            if (expectToPay > p.oopMax) {
                expectToPay = p.oopMax;
            }
        }
        expectToPay += p.premium * 12;
        
        return expectToPay;
    }
}
