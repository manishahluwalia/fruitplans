package fruit.health.client.mvp;

import com.google.gwt.user.client.ui.IsWidget;

public interface BaseView<P> extends IsWidget
{
    void setPresenter(P presenter);
}
