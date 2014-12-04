package fruit.health.client.view.desktop.views;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
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
	
    private static HomeViewUiBinder uiBinder = GWT
			.create(HomeViewUiBinder.class);
	
	@UiTemplate("CompareView.ui.xml")
	interface HomeViewUiBinder extends UiBinder<Widget, CompareViewImpl> {
	}

	public CompareViewImpl(Runnable doneCallback) {
		initWidget(uiBinder.createAndBindUi(this));
		VisualizationUtils.loadVisualizationApi(doneCallback, BarChart.PACKAGE);

		DOM.sinkEvents(numDocVisitsInput, Event.ONCHANGE);
        DOM.setEventListener(numDocVisitsInput, new EventListener()
        {   
            @Override
            public void onBrowserEvent(Event event)
            {   
                presenter.onNumDocVisitsChanged(readVal(numDocVisitsInput));
            }
        });

		DOM.sinkEvents(numRxsInput, Event.ONCHANGE);
        DOM.setEventListener(numRxsInput, new EventListener()
        {   
            @Override
            public void onBrowserEvent(Event event)
            {   
                presenter.onNumRxsChanged(readVal(numRxsInput));
            }
        });

		DOM.sinkEvents(numHospitalizationsInput, Event.ONCHANGE);
        DOM.setEventListener(numHospitalizationsInput, new EventListener()
        {   
            @Override
            public void onBrowserEvent(Event event)
            {   
                presenter.onNumHospitalizationsChanged(readVal(numHospitalizationsInput));
            }
        });
        
        Window.addResizeHandler(new ResizeHandler() {
        	Timer resizeTimer = new Timer() {  
        		@Override
        		public void run() {
        			onWindowResize();
        		}
        	};

        	@Override
        	public void onResize(ResizeEvent event) {
        		resizeTimer.cancel();
        		resizeTimer.schedule(250);
        	}
        });
	}

	private Integer readVal(InputElement input) {
		String val = readElementVal(input);
		if (null==val || val.isEmpty()) {
			return null;
		}
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	private native String readElementVal(InputElement input) /*-{
		return input.value;
	}-*/;   

	@UiField InputElement numDocVisitsInput;
	@UiField InputElement numRxsInput;
	@UiField InputElement numHospitalizationsInput;
	@UiField SimplePanel chartHolder;
	
	@UiField DivElement fbShareButton;
	
    private DataTable table;
    private BarChart chart;
    private Options options;
    private boolean reDrawChart = false;

	@Override
    public void prepareFor(int numPlans, int maxDocVisits, int maxRxs, int maxHospiDays)
    {
		numDocVisitsInput.setAttribute("min", "0");
		numDocVisitsInput.setAttribute("max", Integer.toString(maxDocVisits));

		numRxsInput.setAttribute("min", "0");
		numRxsInput.setAttribute("max", Integer.toString(maxRxs));

		numHospitalizationsInput.setAttribute("min", "0");
		numHospitalizationsInput.setAttribute("max", Integer.toString(maxHospiDays));
    }
    
    @Override
    public void setScenario(int numDocVisits, int numRxs, int numHospitalizations) {
		numDocVisitsInput.setAttribute("value", Integer.toString(numDocVisits));
		numRxsInput.setAttribute("value", Integer.toString(numRxs));
		numHospitalizationsInput.setAttribute("value", Integer.toString(numHospitalizations));
    }
    
    @Override
    public void showChart(String[] planNames, int[] mins, int[] maxs, int[] customs) {
        options = Options.create();

        options.setWidth(chartHolder.getOffsetWidth());
        options.setHeight(chartHolder.getOffsetHeight());
        options.set3D(true);
        
        options.setTitleColor("#168C8C");
        options.setLegendTextColor("#168C8C");
        
        //options.setTitle("Comparision of Plans");
        
        options.setTitleX("Total Cost to You from your pocket");
        
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
        
        reDrawChart = true;
    }

    private native JavaScriptObject getAnimationOptions()
    /*-{
        return {duration: 1000, easing: 'inAndOut'};
    }-*/;

    private native JavaScriptObject getColors()
    /*-{
        return [{color:'#FFC3BA', darker:'#d88c83'}, {color:'#ACEDED', darker:'#75b6b6'}, {color: '#FFE5BA', darker: '#d8ad83'}];
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

    public void onWindowResize() {
    	if (reDrawChart) {
    		options.setWidth(chartHolder.getOffsetWidth());
    		options.setHeight(chartHolder.getOffsetHeight());
    		chart.draw(table, options);
    	}
    }
    
    @Override
    public void stopRedrawingChart() {
    	reDrawChart = false;
    }
    
    private native void facebookReparse()
    /*-{
        $wnd.FB.XFBML.parse();
    }-*/;
}
