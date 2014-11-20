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
        void onScenarioChange(int newIdx);
    }

    void showChart(String[] planNames, int[] mins, int[] maxs, int[] customs);

    void prepareFor(int numPlans, String[] scenarios);

    void setScenarioIdx(int idx);
}
