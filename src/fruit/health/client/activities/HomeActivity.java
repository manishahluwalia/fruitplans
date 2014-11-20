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
    private static final int BILLABLE_MEDICAL_EXPENSES_FOR_MAX_SCENARIO = 1000000;
    private final List<PlanData> plans;
    private final Scenario customScenario;
    private int numPlans;
    private String[] planNames;
    private int[] mins;
    private int[] maxs;
    private int[] customs;

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
        
        numPlans = plans.size();
        planNames = new String[numPlans];
        mins = new int[numPlans];
        maxs = new int[numPlans];
        customs = new int[numPlans];
        
        for (int i=0; i<numPlans; i++) {
            PlanData p = plans.get(i);
            planNames[i] = p.planName;
            mins[i] = expectToPay(p, BILLABLE_MEDICAL_EXPENSES_FOR_MIN_SCENARIO);
            maxs[i] = expectToPay(p, BILLABLE_MEDICAL_EXPENSES_FOR_MAX_SCENARIO);
            customs[i] = expectToPay(p, customScenario.getMedicalExpenses());
        }
        
        view.prepareFor(numPlans, new String[]{"Very Healthy","Minor Illnesses","Some major issues", "Very Sick"});
        
        if (numPlans>0) {
            showData();
            view.setScenarioIdx(1);            
        }
    }
    
    private void showData() {
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

    @Override
    public void onScenarioChange(int newIdx)
    {
        switch (newIdx) {
        case 0:
            customScenario.numDocVisits = 0;
            customScenario.numDaysInHospital = 0;
            customScenario.numRxs = 0;
            break;
            
        case 1:
            customScenario.numDocVisits = 2;
            customScenario.numDaysInHospital = 1;
            customScenario.numRxs = 4;
            break;

        case 2:
            customScenario.numDocVisits = 4;
            customScenario.numDaysInHospital = 3;
            customScenario.numRxs = 8;
            break;

        case 3:
            customScenario.numDocVisits = 20;
            customScenario.numDaysInHospital = 100;
            customScenario.numRxs = 100;
            break;
        }
        
        for (int i=0; i<numPlans; i++) {
            PlanData p = plans.get(i);
            customs[i] = expectToPay(p, customScenario.getMedicalExpenses());
        }

        showData();
    }
}
