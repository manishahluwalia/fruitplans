package fruit.health.client.activities;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import fruit.health.client.entities.PlanData;
import fruit.health.client.entities.Scenario;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BaseActivity;
import fruit.health.client.places.editScenario;
import fruit.health.client.places.enterPlan;
import fruit.health.client.places.home;
import fruit.health.client.view.HomeView;
import fruit.health.client.view.HomeView.Presenter;
import fruit.health.shared.util.RunnableWithArg;

public class HomeActivity extends BaseActivity<HomeView, Presenter> implements Presenter
{
    protected static final Logger logger = Logger.getLogger(HomeActivity.class.getName());
    private static final int BILLABLE_MEDICAL_EXPENSES_FOR_MIN_SCENARIO = 0;
    private static final int BILLABLE_MEDICAL_EXPENSES_FOR_MAX_SCENARIO = 100000;
    private final List<PlanData> plans;
    private final Scenario customScenario;

    public HomeActivity (home place, AppGinjector injector)
    {
        super(injector);
        this.plans  = injector.getGlobalsHolder().getPlans();
        customScenario = injector.getGlobalsHolder().getCustomScenario();
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
        
        int numPlans = plans.size();
        String[] planNames = new String[numPlans];
        int[] mins = new int[numPlans];
        int[] maxs = new int[numPlans];
        int[] customs = new int[numPlans];
        
        view.prepareFor(numPlans);
        
        for (int i=0; i<numPlans; i++) {
            PlanData p = plans.get(i);
            planNames[i] = p.planName;
            mins[i] = expectToPay(p, BILLABLE_MEDICAL_EXPENSES_FOR_MIN_SCENARIO);
            maxs[i] = expectToPay(p, BILLABLE_MEDICAL_EXPENSES_FOR_MAX_SCENARIO);
            customs[i] = expectToPay(p, customScenario.getMedicalExpenses());
        }
        
        view.showChart(planNames, mins, maxs, customs);
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

    @Override
    public void customScenarioClicked()
    {
        loginStateManager.goTo(new editScenario(customScenario));
    }
}
