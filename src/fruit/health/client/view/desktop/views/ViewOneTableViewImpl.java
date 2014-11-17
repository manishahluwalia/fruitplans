package fruit.health.client.view.desktop.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import fruit.health.client.util.JsniUtils;
import fruit.health.client.view.ViewOneTableView;
import fruit.health.client.view.ViewOneTableView.Presenter;
import fruit.health.client.view.desktop.BaseViewImpl;
import fruit.health.shared.entities.Table;
import fruit.health.shared.util.RunnableWithArg;

public class ViewOneTableViewImpl extends BaseViewImpl<Presenter> implements ViewOneTableView
{
    private static ViewOneTableViewUiBinder uiBinder = GWT.create(ViewOneTableViewUiBinder.class);

    @UiTemplate("ViewOneTableView.ui.xml")
    interface ViewOneTableViewUiBinder extends UiBinder<Widget, ViewOneTableViewImpl> {
    }

    public ViewOneTableViewImpl()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    
    @Override
    public void showTable(Table table)
    {
        String[] cols = table.getSchema().split(",");
        JavaScriptObject columns = JavaScriptObject.createArray(cols.length);
        int i=0;
        for (String col : cols)
        {
            String title = col.substring(0, col.indexOf(':'));
            JavaScriptObject column = JavaScriptObject.createObject();
            JsniUtils.setMapValue(column, "title", title);
            JsniUtils.setArrayIndex(columns, i++, column);
        }
        
        renderTable(columns);
    }

    private static class DataTables_Data extends JavaScriptObject {
        protected DataTables_Data() {}
        public final native int getDraw() /*-{ return this.draw; }-*/;
        public final native int getStart() /*-{ return this.start; }-*/;
        public final native int getLength() /*-{ return this.length; }-*/;
    }

    private native void makeFunctionCall(JavaScriptObject func, JavaScriptObject param)
    /*-{
        func(param);
    }-*/;
    
    private void getData(final DataTables_Data data, final JavaScriptObject callback, final JavaScriptObject settings)
    {
        final int start = data.getStart();
        final int lengthRequested = data.getLength();
        presenter.getData(start, lengthRequested, new RunnableWithArg<String>() {

            @Override
            public void run(String response)
            {
                GWT.log("Response: " + response);
                JSONObject recv = JSONParser.parseStrict(response).isObject();
                JSONValue totalRows = recv.get("totalRows");
                JSONArray rows = recv.get("rows").isArray();

                JSONObject transformed = new JSONObject();
                transformed.put("recordsTotal", totalRows);
                transformed.put("recordsFiltered", totalRows);
                JSONArray data = new JSONArray();
                
                for (int i=0; i<rows.size(); i++)
                {
                    JSONArray f = rows.get(i).isObject().get("f").isArray();
                    JSONArray row = new JSONArray();
                    for (int j=0; j<f.size(); j++)
                    {
                        row.set(j, f.get(j).isObject().get("v"));
                    }
                    data.set(i, row);
                }
                
                transformed.put("data", data);
                makeFunctionCall(callback, transformed.getJavaScriptObject());
            }
        });
    }
    

    private native JavaScriptObject renderTable(JavaScriptObject columns)
    /*-{
        var self = this;
        table = $wnd.jQuery("#view1Table_dataTable").DataTable({
            destroy: true,
            paginationType: "scrolling",
            scroller: {
                loadingIndicator: true
            },
            scrollY: "300px",
            scrollCollapse: true,
            scrollX: true,
            dom : "frtiS",
            searching : false,
            filter : true,
            info : true,
            serverSide : true,
            columns : columns,
            ajax : function (data, callback, settings) {
                console.log("settings",settings);
                $entry(self.@fruit.health.client.view.desktop.views.ViewOneTableViewImpl::getData(Lfruit/health/client/view/desktop/views/ViewOneTableViewImpl$DataTables_Data;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(data,callback,settings));
            }
        });
        return table;
    }-*/;
}
