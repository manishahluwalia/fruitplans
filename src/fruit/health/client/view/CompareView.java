package fruit.health.client.view;

import fruit.health.client.mvp.BaseView;

public interface CompareView extends BaseView<CompareView.Presenter>
{
    public static interface Presenter
    {
        void enterPlanClicked();
        
        void customScenarioClicked();
        
        void onNumDocVisitsChanged(int numDocVisits);
        void onNumRxsChanged(int numRxs);
        void onNumHospiDaysChanged(int numHospiDays);
    }

    void showChart(String[] planNames, int[] mins, int[] maxs, int[] customs);

    void setScenario(int numDocVisits, int numRxs, int numHospiDays);
    
    void setShareLink(String link);

    void updateCustomScenario(int[] customs);

    void prepareFor(int numPlans, int maxDocVisits, int maxRxs, int maxHospiDays);
}
