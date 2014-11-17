package fruit.health.client.view;

import java.util.LinkedList;

import fruit.health.client.mvp.BaseView;
import fruit.health.shared.entities.Table;
import fruit.health.shared.util.Pair;

public interface ViewTablesView extends BaseView<ViewTablesView.Presenter>
{

    public interface Presenter
    {

        void onTableClicked(Table table);

    }

    void setTables(LinkedList<Pair<Table, String>> linkedList);

}
