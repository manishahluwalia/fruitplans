package fruit.health.client.activities;

import java.util.logging.Logger;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import fruit.health.client.entities.PlanData;
import fruit.health.client.events.PlanAddedEvent;
import fruit.health.client.events.PlanEditDoneEvent;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BaseActivity;
import fruit.health.client.places.enterPlan;
import fruit.health.client.util.InputValidation;
import fruit.health.client.view.EnterPlanView;
import fruit.health.client.view.EnterPlanView.Presenter;
import fruit.health.shared.util.RunnableWithArg;

public class EnterPlanActivity extends BaseActivity<EnterPlanView, Presenter> implements Presenter
{
    protected static final Logger logger = Logger.getLogger(EnterPlanActivity.class.getName());

    private final PlanData planData;
    private final PlanData originalPlan;
    private final boolean isAdd;
    
    public EnterPlanActivity (enterPlan place, AppGinjector injector)
    {
        super(injector);
        if (null==place.getPlan()) {
            planData=new PlanData();
            isAdd=true;
            originalPlan = null;
        } else {
            planData = place.getPlan();
            originalPlan = planData.clone(new PlanData());
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
        planData.premium = InputValidation.readIntVal(val, "premium", 0, 10000, viewMaster);
        adjustButtons();
    }

    @Override
    public void onDeductibleChanged(String val)
    {
        planData.deductible = InputValidation.readIntVal(val, "deductible", 0, 10000, viewMaster);
        adjustButtons();
    }

    @Override
    public void onCopayChanged(String val)
    {
        planData.copay = InputValidation.readIntVal(val, "co-pay", 0, 100, viewMaster);
        adjustButtons();
    }

    @Override
    public void onOopMaxChanged(String val)
    {
        planData.oopMax = InputValidation.readIntVal(val, "out-of-pocket max", 0, 100000, viewMaster);
        adjustButtons();
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
    public void onCancelPressed()
    {
        if (!isAdd) {
            originalPlan.clone(planData);
        }
        
        eventBus.fireEvent(new PlanEditDoneEvent());
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
        
        eventBus.fireEvent(new PlanEditDoneEvent());
    }
}
