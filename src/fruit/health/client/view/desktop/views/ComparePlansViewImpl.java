package fruit.health.client.view.desktop.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

import fruit.health.client.view.ComparePlansView;
import fruit.health.client.view.desktop.BaseViewImpl;
import fruit.health.client.view.desktop.DesktopBrowserViewMaster;

public class ComparePlansViewImpl extends BaseViewImpl<ComparePlansView.Presenter> implements ComparePlansView {
	
	private static ComparePlansViewUiBinder uiBinder = GWT
			.create(ComparePlansViewUiBinder.class);
	
	@UiTemplate("ComparePlansView.ui.xml")
	interface ComparePlansViewUiBinder extends UiBinder<Widget, ComparePlansViewImpl> {
	}

	public ComparePlansViewImpl(DesktopBrowserViewMaster desktopBrowserViewMaster) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField FlexTable table;
	
    @Override
    public void prepareFor(int numPlans)
    {
        table.clear();
        table.setText(0, 0, "Name of Plan");
        table.setText(1, 0, "Premiums");
        table.setText(2, 0, "Use preventive care only");
        table.setText(3, 0, "Use a few services");
        table.setText(4, 0, "Serious medical issues");
    }

    @Override
    public void showPlan(int planNum, String planName, int premiums, int prevOnly, int fewServices, int seriousUse)
    {
        table.setText(0, planNum+1, planName);
        table.setText(1, planNum+1, Integer.toString(premiums));
        table.setText(2, planNum+1, Integer.toString(prevOnly));
        table.setText(3, planNum+1, Integer.toString(fewServices));
        table.setText(4, planNum+1, Integer.toString(seriousUse));
    }
}
