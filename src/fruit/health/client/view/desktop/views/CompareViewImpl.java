package fruit.health.client.view.desktop.views;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.BarChart;
import com.google.gwt.visualization.client.visualizations.BarChart.Options;

import fruit.health.client.view.CompareView;
import fruit.health.client.view.CompareView.Presenter;
import fruit.health.client.view.desktop.BaseViewImpl;

public class CompareViewImpl extends BaseViewImpl<Presenter> implements CompareView {
	private static final Logger logger = Logger.getLogger(CompareViewImpl.class.getName());
	
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
	}

	@UiField DivElement plansPara;
	@UiField SimplePanel chartHolder;
	
	@UiField DivElement fbShareButton;
	
    private DataTable table;
    private BarChart chart;
    private Options options;

	@Override
    public void prepareFor(int numPlans, int maxDocVisits, int maxRxs, int maxHospiDays)
    {
        if (0==numPlans) {
            this.plansPara.setAttribute("hidden", "hidden");
        } else {
            this.plansPara.removeAttribute("hidden");
            prepSlider(maxDocVisits, maxRxs, maxHospiDays);
        }
    }
    
    private native void prepSlider(int maxDocVisits, int maxRxs, int maxHospitalizations)
    /*-{
        var self = this;
        
        var slider1 = $wnd.jQuery("#numDocVisitsSlider").slider({min:0, max:maxDocVisits, step: 1});
        slider1.slider("pips" , { rest: false });
        slider1.slider("float");
        slider1.on("slidechange", function (event, ui) {
            self.@fruit.health.client.view.desktop.BaseViewImpl::presenter.@fruit.health.client.view.CompareView.Presenter::onNumDocVisitsChanged(I)(ui.value);
        });
        
        var slider2 = $wnd.jQuery("#numRxsSlider").slider({min:0, max:maxRxs, step: 1});
        slider2.slider("pips" , { rest: false });
        slider2.slider("float");
        slider2.on("slidechange", function (event, ui) {
            self.@fruit.health.client.view.desktop.BaseViewImpl::presenter.@fruit.health.client.view.CompareView.Presenter::onNumRxsChanged(I)(ui.value);
        });
        
        var slider3 = $wnd.jQuery("#numHospitalizationsSlider").slider({min:0, max:maxHospitalizations, step: 1});
        slider3.slider("pips" , { rest: false });
        slider3.slider("float");
        slider3.on("slidechange", function (event, ui) {
            self.@fruit.health.client.view.desktop.BaseViewImpl::presenter.@fruit.health.client.view.CompareView.Presenter::onNumHospitalizationsChanged(I)(ui.value);
        });
    }-*/;
    
    private native void setSliderValues(int numDocVisits, int numRxs, int numHospitalizations) /*-{
        $wnd.jQuery("#numDocVisitsSlider").slider("value", numDocVisits);
        $wnd.jQuery("#numRxsSlider").slider("value", numRxs);
        $wnd.jQuery("#numHospitalizationsSlider").slider("value", numHospitalizations);
    }-*/;

    @Override
    public void setScenario(int numDocVisits, int numRxs, int numHospitalizations) {
        setSliderValues(numDocVisits, numRxs, numHospitalizations);
    }
    
    @Override
    public void showChart(String[] planNames, int[] mins, int[] maxs, int[] customs) {
        options = Options.create();

//        options.setOption("chartArea", getChartArea());
        options.setWidth(CHART_WIDTH);
        options.setHeight(CHART_HEIGHT);
        options.set3D(true);
        
        String bgColor = Window.Location.getParameter("color");
        if (null!=bgColor && !bgColor.isEmpty()) {
            options.setBackgroundColor(bgColor);
            logger.warning("got color: " + bgColor);
        }
        //options.setBackgroundColor("#2a95a2");
        //options.setBackgroundColor("#2c7fa3");
        //options.setBackgroundColor("#2b8fa2");
        
        //options.setTitle("Comparision of Plans");
        
        options.setTitleX("$ spent by you, per year");
        
        //options.setTitleY("Plan");
        options.setOption("animation", getAnimationOptions());
        options.setOption("colors", getColors());
        
        table = DataTable.create();
        table.addColumn(ColumnType.STRING, "Plan name");
        table.addColumn(ColumnType.NUMBER, "Perfect Health");
        table.addColumn(ColumnType.NUMBER, "Your estimate");
        table.addColumn(ColumnType.NUMBER, "Very Sick");        
        table.addRows(planNames.length);
        
        for (int i=0; i<planNames.length; i++) {
            table.setValue(i, 0, planNames[i]);
            table.setValue(i, 1, mins[i]);
            table.setValue(i, 2, customs[i]);
            table.setValue(i, 3, maxs[i]);
        }
        
        chart = new BarChart(table,options);
        chartHolder.clear();
        chartHolder.add(chart);
    }

    private native JavaScriptObject getChartArea()
    /*-{
        return {height: '100%', width: '60%', left:0, top:0};
    }-*/;

    private native JavaScriptObject getAnimationOptions()
    /*-{
        return {duration: 1000, easing: 'inAndOut'};
    }-*/;

    private native JavaScriptObject getColors()
    /*-{
        return [{color:'#008000', darker:'#005900'}, {color:'#0000ff', darker:'#0000b2'}, {color: '#800000', darker: '#590000'}];
    }-*/;

    @Override
    public void updateCustomScenario(int[] customs) {
        for (int i=0; i<customs.length; i++) {
            table.setValue(i, 2, customs[i]);
        }
        
        chart.draw(table, options);
    }
    
    @UiHandler("enterPlan")
    public void enterPlanClicked(ClickEvent e) {
        presenter.enterPlanClicked();
    }
    
    @Override
    public void setShareLink(String link) {
        fbShareButton.setAttribute("data-href", link);
        try {
            facebookReparse();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not initialize facebook button", e);
        }
    }

    private native void facebookReparse()
    /*-{
        $wnd.FB.XFBML.parse();
    }-*/;
}
