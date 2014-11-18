package fruit.health.client.view;

import fruit.health.client.entities.PlanData;
import fruit.health.client.mvp.BaseView;

public interface HomeView extends BaseView<HomeView.Presenter>
{
    public static interface Presenter
    {

        void enterDataClicked();
        void onPlanClicked(PlanData p);
        
    }

    void prepareFor(int numPlans);

    public void showPlan(PlanData plan, int planNum, String planName, int premiums, int prevOnly, int fewServices, int seriousUse);
}
