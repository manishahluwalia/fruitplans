package fruit.health.client.activities;

import java.util.logging.Logger;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import fruit.health.client.entities.Scenario;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BaseActivity;
import fruit.health.client.places.editScenario;
import fruit.health.client.places.home;
import fruit.health.client.util.InputValidation;
import fruit.health.client.view.EditScenarioView;
import fruit.health.client.view.EditScenarioView.Presenter;
import fruit.health.shared.util.RunnableWithArg;

public class EditScenarioActivity extends BaseActivity<EditScenarioView, Presenter> implements Presenter
{
    protected static final Logger logger = Logger.getLogger(EditScenarioActivity.class.getName());

    private final Scenario scenario;
    
    public EditScenarioActivity (editScenario place, AppGinjector injector)
    {
        super(injector);
        scenario = place.getScenario();
    }
    
    @Override
    protected void createView (RunnableWithArg<EditScenarioView> callback)
    {
        viewMaster.getEditScenarioView(callback);
    }

    @Override
    public void start (AcceptsOneWidget panel)
    {
        super.start(panel);
        if (null==scenario) {
            loginStateManager.goTo(new home());
        }
        view.showData(scenario);
        adjustButtons();
    }

    @Override
    public void onDonePressed()
    {
        loginStateManager.goTo(new home());
    }

    @Override
    public void onGenderChanged(boolean isFemale)
    {
        scenario.isFemale = isFemale;
    }

    @Override
    public void onAgeChanged(String value)
    {
        scenario.age = InputValidation.readIntVal(value, "age", 0, 150, viewMaster);
        adjustButtons();
    }

    @Override
    public void onNumDocVistsChanged(String value)
    {
        scenario.numDocVisits = InputValidation.readIntVal(value, "number of doctor visits", 0, 100, viewMaster);
        adjustButtons();
    }

    @Override
    public void onNumRxsChanged(String value)
    {
        scenario.numRxs = InputValidation.readIntVal(value, "number of prescriptions", 0, 1000, viewMaster);
        adjustButtons();
    }

    @Override
    public void onNumHospiDaysChanged(String value)
    {
        scenario.numDaysInHospital =  InputValidation.readIntVal(value, "number of days in hospital", 0, 365, viewMaster);
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
        if (null==scenario.age ||
                null==scenario.numDocVisits ||
                null==scenario.numRxs ||
                null==scenario.numDaysInHospital
                ) {
            return false;
        }
        
        return true;
    }
}
