package fruit.health.client.view;

import fruit.health.client.entities.PlanData;
import fruit.health.client.mvp.BaseView;

public interface EnterPlanView extends BaseView<EnterPlanView.Presenter>
{
    public static interface Presenter
    {
        void onNameChanged(String name);
        void onPremiumChanged(Integer val);
        void onDeductibleChanged(Integer val);
        void onCopayChanged(Double val);
        void onOopMaxChanged(Integer val);
        void onCancelPressed();
        void onAddPressed();
        void onComparePressed();
    }

    void showData(PlanData plan);

    void enableButtons(boolean enable);
}
