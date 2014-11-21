package fruit.health.client.activities;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import fruit.health.client.entities.PlanData;
import fruit.health.client.entities.Scenario;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BaseActivity;
import fruit.health.client.places.compare;
import fruit.health.client.places.editScenario;
import fruit.health.client.places.enterPlan;
import fruit.health.client.util.URLCreator;
import fruit.health.client.view.CompareView;
import fruit.health.client.view.CompareView.Presenter;
import fruit.health.shared.util.RunnableWithArg;

public class CompareActivity extends BaseActivity<CompareView, Presenter> implements Presenter
{
    protected static final Logger logger = Logger.getLogger(CompareActivity.class.getName());
    
    private static final int BILLABLE_MEDICAL_EXPENSES_FOR_MIN_SCENARIO = 0;
    private static final int BILLABLE_MEDICAL_EXPENSES_FOR_MAX_SCENARIO = 100000000;
    
    private final compare place;
    private final List<PlanData> plans;
    private final Scenario customScenario;
    private int numPlans;
    private String[] planNames;
    private int[] mins;
    private int[] maxs;
    private int[] customs;

    private final URLCreator urlCreator;

    public CompareActivity (compare place, AppGinjector injector)
    {
        super(injector);
        this.place = place;
        this.plans  = place.getPlans();
        injector.getGlobalsHolder().setPlans(plans);
        customScenario = injector.getGlobalsHolder().getCustomScenario();
        urlCreator = injector.getURLCreator();
    }

    @Override
    protected void createView (RunnableWithArg<CompareView> callback)
    {
        viewMaster.getCompareView(callback);
    }

    @Override
    public void start (AcceptsOneWidget panel)
    {
        super.start(panel);
        
        view.setShareLink(urlCreator.getLinkToPlace(place));
        
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
        
        view.prepareFor(numPlans, new String[]{"Perfect Health","Minor Illnesses","Some major issues", "Very Sick"});
        
        if (numPlans>0) {
            view.showChart(planNames, mins, maxs, customs);
            view.setScenarioIdx(1);            
        }
    }

    @Override
    public void enterPlanClicked()
    {
        loginStateManager.goTo(new enterPlan(null));
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

        view.updateCustomScenario(customs);
    }
}
