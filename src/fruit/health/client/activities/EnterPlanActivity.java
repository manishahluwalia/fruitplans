package fruit.health.client.activities;

import java.util.logging.Logger;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import fruit.health.client.entities.PlanData;
import fruit.health.client.events.PlanAddedEvent;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BaseActivity;
import fruit.health.client.places.comparePlans;
import fruit.health.client.places.enterPlan;
import fruit.health.client.places.home;
import fruit.health.client.view.EnterPlanView;
import fruit.health.client.view.EnterPlanView.Presenter;
import fruit.health.shared.util.RunnableWithArg;

public class EnterPlanActivity extends BaseActivity<EnterPlanView, Presenter> implements Presenter
{
    protected static final Logger logger = Logger.getLogger(EnterPlanActivity.class.getName());

    private final PlanData planData;
    private final boolean isAdd;
    
    public EnterPlanActivity (enterPlan place, AppGinjector injector)
    {
        super(injector);
        if (null==place.getPlan()) {
            planData=new PlanData();
            planData.planName = "Option " + injector.getGlobalsHolder().planNumber++;
            isAdd=true;
        } else {
            planData = place.getPlan();
            isAdd=false;
        }
    }
    
    @Override
    protected void createView (RunnableWithArg<EnterPlanView> callback)
    {
        viewMaster.getEnterPlanView(callback);
    }

    @Override
    public void start (AcceptsOneWidget panel)
    {
        super.start(panel);
        view.showData(planData);
        adjustButtons();
    }

    @Override
    public void onNameChanged(String name)
    {
        planData.planName = name;
        adjustButtons();
    }
    
    @Override
    public void onPremiumChanged(String val)
    {
        planData.premium = readIntVal(val, "premium", 0, 10000);
        adjustButtons();
    }

    @Override
    public void onDeductibleChanged(String val)
    {
        planData.deductible = readIntVal(val, "deductible", 0, 10000);
        adjustButtons();
    }

    @Override
    public void onCopayChanged(String val)
    {
        planData.copay = readIntVal(val, "co-pay", 0, 100);
        adjustButtons();
    }

    @Override
    public void onOopMaxChanged(String val)
    {
        planData.oopMax = readIntVal(val, "out-of-pocket max", 0, 100000);
        adjustButtons();
    }

    private Integer readIntVal(String val, String field, Integer min, Integer max)
    {
        logger.warning("Value: " + val + " for " + field);
        if (null==val || val.isEmpty()) {
            return null;
        }
        
        int v;
        try
        {
            v = Integer.parseInt(val);
        }
        catch (Exception e)
        {
            viewMaster.alertDialog("Bad Input", "Bad value entered for " + field, null, null);
            return null;
        }
        
        if (null!=min && v<min)
        {
            viewMaster.alertDialog("Bad Input", "Bad value entered for " + field+ ". Must be >= " + min, null, null);
            return null;
        }
        
        if (null!=max && v>max)
        {
            viewMaster.alertDialog("Bad Input", "Bad value entered for " + field+ ". Must be <= " + max, null, null);
            return null;
        }

        return v;
    }

    private void adjustButtons()
    {
        if (isDone()) {
            view.enableButtons(true);
        } else {
            view.enableButtons(false);
        }
    }
    
    private boolean isDone()
    {
        if (null==planData.planName ||
                null==planData.premium ||
                null==planData.deductible ||
                null==planData.copay ||
                null==planData.oopMax
                ) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void onDonePressed()
    {
        if (!isDone()) {
            return;
        }
        
        if (isAdd) {
            eventBus.fireEvent(new PlanAddedEvent(planData));
        }
        
        loginStateManager.goTo(new home());
    }

    @Override
    public void onAddPressed()
    {
        if (!isDone()) {
            return;
        }
        
        if (isAdd) {
            eventBus.fireEvent(new PlanAddedEvent(planData));
        }
        
        eventBus.fireEvent(new PlanAddedEvent(planData));
        loginStateManager.goTo(new enterPlan(null));
    }

    @Override
    public void onComparePressed()
    {
        if (!isDone()) {
            return;
        }
        
        if (isAdd) {
            eventBus.fireEvent(new PlanAddedEvent(planData));
        }
        
        loginStateManager.goTo(new comparePlans());
    }
}
