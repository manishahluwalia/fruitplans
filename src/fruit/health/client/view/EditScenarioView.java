package fruit.health.client.view;

import fruit.health.client.entities.Scenario;
import fruit.health.client.mvp.BaseView;


public interface EditScenarioView extends BaseView<EditScenarioView.Presenter>
{
    public static interface Presenter
    {
        void onDonePressed();

        void onAgeChanged(String value);
        void onNumDocVistsChanged(String value);
        void onNumRxsChanged(String value);
        void onNumHospiDaysChanged(String value);
        void onGenderChanged(boolean isFemale);
    }

    void showData(Scenario scenario);
    void enableButtons(boolean enable);
}
