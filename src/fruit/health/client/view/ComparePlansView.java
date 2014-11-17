package fruit.health.client.view;

import fruit.health.client.mvp.BaseView;

public interface ComparePlansView extends BaseView<ComparePlansView.Presenter>
{
    public static interface Presenter
    {
    }

    void prepareFor(int numPlans);

    public void showPlan(int planNum, String planName, int premiums, int prevOnly, int fewServices, int seriousUse);
}
