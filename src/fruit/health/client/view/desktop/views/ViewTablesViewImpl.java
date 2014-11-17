package fruit.health.client.view.desktop.views;

import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fruit.health.client.view.ViewTablesView;
import fruit.health.client.view.ViewTablesView.Presenter;
import fruit.health.client.view.desktop.BaseViewImpl;
import fruit.health.shared.dto.TableState;
import fruit.health.shared.entities.Table;
import fruit.health.shared.util.Pair;

public class ViewTablesViewImpl extends BaseViewImpl<Presenter> implements ViewTablesView
{
    private static ViewTablesViewUiBinder uiBinder = GWT.create(ViewTablesViewUiBinder.class);

    @UiTemplate("ViewTablesView.ui.xml")
    interface ViewTablesViewUiBinder extends UiBinder<Widget, ViewTablesViewImpl> {
    }

    @UiField DivElement tableTable;
    
    public ViewTablesViewImpl()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setTables(LinkedList<Pair<Table, String>> tables)
    {
        tableTable.removeAllChildren();
        
        int n=0;
        for (final Pair<Table, String> pair : tables)
        {
            n++;
            SafeHtmlBuilder builder = new SafeHtmlBuilder();
            builder.appendHtmlConstant("<h3 class=\"no-margin col-xs-4 pull-left text-white\">")
                   .appendEscaped(pair.getA().getDescription())
                   .appendHtmlConstant("</h3>");
            builder.appendHtmlConstant("<h3 class=\"no-margin col-xs-4 pull-left text-teal\">")
                   .appendEscaped(pair.getB())
                   .appendHtmlConstant("</h3>");
            builder.appendHtmlConstant("<h3 class=\"no-margin col-xs-2 pull-right\">")
                   .appendEscaped(getStateDesc(pair.getA().getState()))
                   .appendHtmlConstant("</h3>");
            HTMLPanel div = new HTMLPanel(builder.toSafeHtml());
            div.addStyleName("row");
            div.addStyleName("border-top-dotted");
            if (tables.size()==n)
            {
                div.addStyleName("border-bottom-dotted");
            }
            div.addStyleName("contact-content");
            
            DOM.sinkEvents(div.getElement(), Event.ONCLICK);
            DOM.setEventListener(div.getElement(), new EventListener()
            {
                @Override
                public void onBrowserEvent(Event event)
                {
                    presenter.onTableClicked(pair.getA());
                }
            });
            tableTable.appendChild(div.getElement());
        }
    }

    private String getStateDesc(TableState state)
    {
        switch (state)
        {
        case CLEANING_FAILED:
        case DECIPHERING_FAILED:
        case LOADING_TO_DB_FAILED:
        case LOADING_TO_STORAGE_FAILED:
            return "Error";
            
        case READY:
            return "Loaded";
            
        case LOADING_TO_DB:
        default:
            return "Loading...";
        }
    }

}
