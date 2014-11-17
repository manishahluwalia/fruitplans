package fruit.health.client.view;

import fruit.health.client.mvp.BaseView;
import fruit.health.shared.entities.Table;
import fruit.health.shared.util.RunnableWithArg;

public interface ViewOneTableView extends BaseView<ViewOneTableView.Presenter>
{

    public interface Presenter
    {
        void getData(int start, int lengthRequested, RunnableWithArg<String> callback);
    }

    void showTable(Table table);


}
