package fruit.health.client.view.desktop.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.BarChart;
import com.google.gwt.visualization.client.visualizations.BarChart.Options;

import fruit.health.client.util.JsniUtils;
import fruit.health.client.view.CompareView;
import fruit.health.client.view.CompareView.Presenter;
import fruit.health.client.view.desktop.BaseViewImpl;

public class CompareViewImpl extends BaseViewImpl<Presenter> implements CompareView {
	
	private static final int CHART_WIDTH = 600;
	private static final int CHART_HEIGHT = 400;
	
    private static HomeViewUiBinder uiBinder = GWT
			.create(HomeViewUiBinder.class);
	
	@UiTemplate("CompareView.ui.xml")
	interface HomeViewUiBinder extends UiBinder<Widget, CompareViewImpl> {
	}

	public CompareViewImpl(Runnable doneCallback) {
		initWidget(uiBinder.createAndBindUi(this));
		VisualizationUtils.loadVisualizationApi(doneCallback, BarChart.PACKAGE);
		slider.setId("scenarioChoosingSlider");
	}

	@UiField DivElement plansPara;
	@UiField SimplePanel chartHolder;
	@UiField DivElement slider;
	
	@UiField InputElement linkToPage;

	@Override
    public void prepareFor(int numPlans, String[] scenarios)
    {
        if (0==numPlans) {
            this.plansPara.setAttribute("hidden", "hidden");
        } else {
            this.plansPara.removeAttribute("hidden");
            slider.removeAllChildren();
            prepSlider(JsniUtils.toJsArray(scenarios));
        }
    }
    
    private void onSliderChange(int newIdx) {
        presenter.onScenarioChange(newIdx);
    }
    
    private native JavaScriptObject prepSlider(JavaScriptObject scenarios)
    /*-{
        var self = this;
        var slider = $wnd.jQuery("#scenarioChoosingSlider").slider({min:0, max:scenarios.length-1, step: 1});
        slider.slider("pips" , { rest: "label", labels: scenarios });
        slider.on("slidechange", function (event, ui) {
            self.@fruit.health.client.view.desktop.views.CompareViewImpl::onSliderChange(I)(ui.value);
        });
        return slider;
    }-*/;

    @Override
    public void setScenarioIdx(int idx) {
        setSliderValue(idx);
    }
    
    private native void setSliderValue(int val) /*-{
        var slider = $wnd.jQuery("#scenarioChoosingSlider").slider("value", val);
    }-*/;
    
    @Override
    public void showChart(String[] planNames, int[] mins, int[] maxs, int[] customs) {
        Options options = Options.create();
        options.setWidth(CHART_WIDTH);
        options.setHeight(CHART_HEIGHT);
        options.set3D(true);
        options.setTitle("Comparision of Plans");
        options.setTitleX("$ spent by you, per year");
        options.setTitleY("Plan");
        
        DataTable table = DataTable.create();
        table.addColumn(ColumnType.STRING, "Plan name");
        table.addColumn(ColumnType.NUMBER, "Perfect Health");
        table.addColumn(ColumnType.NUMBER, "Typical");
        table.addColumn(ColumnType.NUMBER, "Severe Illness");        
        table.addRows(planNames.length);
        
        for (int i=0; i<planNames.length; i++) {
            table.setValue(i, 0, planNames[i]);
            table.setValue(i, 1, mins[i]);
            table.setValue(i, 2, customs[i]);
            table.setValue(i, 3, maxs[i]);
        }
        
        BarChart chart = new BarChart(table,options);
        chartHolder.clear();
        chartHolder.add(chart);
    }
    
    @UiHandler("enterPlan")
    public void enterPlanClicked(ClickEvent e) {
        presenter.enterPlanClicked();
    }
    
    @Override
    public void setShareLink(String link) {
        linkToPage.setValue(link);
    }
}
