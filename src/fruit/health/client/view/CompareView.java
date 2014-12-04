package fruit.health.client.view;

import fruit.health.client.mvp.BaseView;

public interface CompareView extends BaseView<CompareView.Presenter>
{
    public static interface Presenter
    {
        void enterPlanClicked();
        
        void onNumDocVisitsChanged(Integer numDocVisits);
        void onNumRxsChanged(Integer numRxs);
        void onNumHospitalizationsChanged(Integer numHospiDays);
    }

    void showChart(String[] planNames, int[] mins, int[] maxs, int[] customs);

    void setScenario(int numDocVisits, int numRxs, int numHospitalizations);
    
    void setShareLink(String link);

    void updateCustomScenario(int[] customs);

    void prepareFor(int numPlans, int maxDocVisits, int maxRxs, int maxHospitalizations);

	void stopRedrawingChart();
}
