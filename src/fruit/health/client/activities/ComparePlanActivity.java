package fruit.health.client.activities;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import fruit.health.client.entities.PlanData;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BaseActivity;
import fruit.health.client.places.comparePlans;
import fruit.health.client.view.ComparePlansView;
import fruit.health.client.view.ComparePlansView.Presenter;
import fruit.health.shared.util.RunnableWithArg;

public class ComparePlanActivity extends BaseActivity<ComparePlansView, Presenter> implements Presenter
{
    protected static final Logger logger = Logger.getLogger(ComparePlanActivity.class.getName());
    private final List<PlanData> plans;
    
    public ComparePlanActivity (comparePlans comparePlans, AppGinjector injector)
    {
        super(injector);
        plans = injector.getGlobalsHolder().getPlans();
    }

    @Override
    protected void createView (RunnableWithArg<ComparePlansView> callback)
    {
        viewMaster.getComparePlansView(callback);
    }

    @Override
    public void start (AcceptsOneWidget panel)
    {
        super.start(panel);
        view.prepareFor(plans.size());
        
        for (int i=0; i<plans.size(); i++) {
            PlanData p = plans.get(i);
            view.showPlan(i, p.planName, p.premium*12, expectToPay(p, 0), expectToPay(p, 2000), expectToPay(p, 25000));
        }
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
