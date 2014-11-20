package fruit.health.client.view;

import fruit.health.client.mvp.BaseView;

public interface HomeView extends BaseView<HomeView.Presenter>
{
    public static interface Presenter
    {

        void enterPlanClicked();
    }

}
