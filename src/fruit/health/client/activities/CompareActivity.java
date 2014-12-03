package fruit.health.client.activities;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import fruit.health.client.entities.PlanData;
import fruit.health.client.entities.Scenario;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BaseActivity;
import fruit.health.client.places.compare;
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
        
        view.prepareFor(numPlans, 24, 120, 2);
        
        if (numPlans>0) {
            view.showChart(planNames, mins, maxs, customs);
            view.setScenario(customScenario.numDocVisits, customScenario.numRxs, customScenario.numHospitalizations);            
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
            expectToPay = (int) ( p.deductible + ( expectedCosts - p.deductible ) * p.copay / 100. );
            if (expectToPay > p.oopMax) {
                expectToPay = p.oopMax;
            }
        }
        expectToPay += p.premium * 12;
        
        return expectToPay;
    }

    
    private void customScenarioChanged()
    {
        
        for (int i=0; i<numPlans; i++) {
            PlanData p = plans.get(i);
            customs[i] = expectToPay(p, customScenario.getMedicalExpenses());
        }

        view.updateCustomScenario(customs);
    }

    @Override
    public void onNumDocVisitsChanged(int numDocVisits)
    {
        customScenario.numDocVisits = numDocVisits;
        customScenarioChanged();
    }

    @Override
    public void onNumRxsChanged(int numRxs)
    {
        customScenario.numRxs = numRxs;
        customScenarioChanged();
    }

    @Override
    public void onNumHospitalizationsChanged(int numHospitalizations)
    {
        customScenario.numHospitalizations = numHospitalizations;
        customScenarioChanged();
    }
    
    @Override
    public void onStop() {
    	view.stopRedrawingChart();
    }
}
