package fruit.health.client.view.desktop.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ParagraphElement;
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

import fruit.health.client.view.HomeView;
import fruit.health.client.view.HomeView.Presenter;
import fruit.health.client.view.desktop.BaseViewImpl;

public class HomeViewImpl extends BaseViewImpl<Presenter> implements HomeView {
	
	private static HomeViewUiBinder uiBinder = GWT
			.create(HomeViewUiBinder.class);
	
	@UiTemplate("HomeView.ui.xml")
	interface HomeViewUiBinder extends UiBinder<Widget, HomeViewImpl> {
	}

	public HomeViewImpl(Runnable doneCallback) {
		initWidget(uiBinder.createAndBindUi(this));
		
		VisualizationUtils.loadVisualizationApi(doneCallback, BarChart.PACKAGE);
	}

	@UiField ParagraphElement plansPara;
	@UiField SimplePanel chartHolder;
    
    @Override
    public void prepareFor(int numPlans)
    {
        if (0==numPlans) {
            this.plansPara.setAttribute("hidden", "hidden");
        } else {
            this.plansPara.removeAttribute("hidden");
            chartHolder.clear();
        }
    }
    
    @Override
    public void showChart(String[] planNames, int[] mins, int[] maxs, int[] customs) {
        Options options = Options.create();
        options.setWidth(400);
        options.setHeight(240);
        options.set3D(true);
        options.setTitle("Comparision of Plans");
        options.setTitleX("$ spent by you, per year");
        options.setTitleY("Plan");
        
        DataTable table = DataTable.create();
        table.addColumn(ColumnType.STRING, "Plan name");
        table.addColumn(ColumnType.NUMBER, "Min");
        table.addColumn(ColumnType.NUMBER, "Custom");
        table.addColumn(ColumnType.NUMBER, "Max");        
        table.addRows(planNames.length);
        
        for (int i=0; i<planNames.length; i++) {
            table.setValue(i, 0, planNames[i]);
            table.setValue(i, 1, mins[i]);
            table.setValue(i, 2, customs[i]);
            table.setValue(i, 3, maxs[i]);
        }
        
        BarChart chart = new BarChart(table,options);
        chartHolder.add(chart);
    }
    
    @UiHandler("enterPlan")
    public void enterDataClicked(ClickEvent e) {
        presenter.enterDataClicked();
    }
}
