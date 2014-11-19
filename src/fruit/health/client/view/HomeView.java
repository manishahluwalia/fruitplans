package fruit.health.client.view;

import fruit.health.client.entities.PlanData;
import fruit.health.client.mvp.BaseView;

public interface HomeView extends BaseView<HomeView.Presenter>
{
    public static interface Presenter
    {

        void enterDataClicked();
        void onPlanClicked(PlanData p);
        
        void customScenarioClicked();
    }

    void prepareFor(int numPlans);

    void showChart(String[] planNames, int[] mins, int[] maxs, int[] customs);
}
