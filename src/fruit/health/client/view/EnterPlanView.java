package fruit.health.client.view;

import fruit.health.client.entities.PlanData;
import fruit.health.client.mvp.BaseView;

public interface EnterPlanView extends BaseView<EnterPlanView.Presenter>
{
    public static interface Presenter
    {
        void onNameChanged(String name);
        void onPremiumChanged(String val);
        void onDeductibleChanged(String val);
        void onCopayChanged(String val);
        void onOopMaxChanged(String val);
        void onDonePressed();
        void onAddPressed();
        void onComparePressed();
    }

    void showData(PlanData plan);

    void enableButtons(boolean enable);
}
