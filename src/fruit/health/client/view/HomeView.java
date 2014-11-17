package fruit.health.client.view;

import java.util.List;

import fruit.health.client.entities.PlanData;
import fruit.health.client.mvp.BaseView;

public interface HomeView extends BaseView<HomeView.Presenter>
{
    public static interface Presenter
    {

        void enterDataClicked();
        void comparePlansClicked();
        void onPlanClicked(PlanData p);
        
    }

    void showPlans(List<PlanData> plans);
}
