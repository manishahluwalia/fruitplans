package fruit.health.client.view;

import fruit.health.client.mvp.BaseView;

public interface CompareView extends BaseView<CompareView.Presenter>
{
    public static interface Presenter
    {
        void enterPlanClicked();
        
        void customScenarioClicked();
        void onScenarioChange(int newIdx);
    }

    void showChart(String[] planNames, int[] mins, int[] maxs, int[] customs);

    void prepareFor(int numPlans, String[] scenarios);

    void setScenarioIdx(int idx);
    
    void setShareLink(String link);

    void updateCustomScenario(int[] customs);
}
