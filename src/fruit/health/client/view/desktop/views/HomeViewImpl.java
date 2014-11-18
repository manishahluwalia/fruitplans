package fruit.health.client.view.desktop.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

import fruit.health.client.entities.PlanData;
import fruit.health.client.view.HomeView;
import fruit.health.client.view.HomeView.Presenter;
import fruit.health.client.view.desktop.BaseViewImpl;
import fruit.health.client.view.desktop.DesktopBrowserViewMaster;

public class HomeViewImpl extends BaseViewImpl<Presenter> implements HomeView {
	
	private static HomeViewUiBinder uiBinder = GWT
			.create(HomeViewUiBinder.class);
	
	@UiTemplate("HomeView.ui.xml")
	interface HomeViewUiBinder extends UiBinder<Widget, HomeViewImpl> {
	}

	public HomeViewImpl(DesktopBrowserViewMaster desktopBrowserViewMaster) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField ParagraphElement plansPara;

    @UiField FlexTable table;
    
    @Override
    public void prepareFor(int numPlans)
    {
        if (0==numPlans) {
            this.plansPara.setAttribute("hidden", "hidden");
        } else {
            this.plansPara.removeAttribute("hidden");
            table.clear();
            table.setText(0, 0, "Name of Plan");
            table.setText(1, 0, "Premiums");
            table.setText(2, 0, "Use preventive care only");
            table.setText(3, 0, "Serious medical issues");
            Anchor customScenario = new Anchor("Custom scenario");
            customScenario.addClickHandler(new ClickHandler()
            {
                @Override public void onClick(ClickEvent event)
                {
                    presenter.customScenarioClicked();
                }
            });
            table.setWidget(4, 0, customScenario);
        }
    }

    @Override
    public void showPlan(final PlanData plan, int planNum, String planName, int premiums, int prevOnly, int seriousUse, int customScenario)
    {
        Anchor link = new Anchor(planName);
        link.addClickHandler(new ClickHandler()
        {
            @Override public void onClick(ClickEvent event)
            {
                presenter.onPlanClicked(plan);
            }
        });
        table.setWidget(0, planNum+1, link);
        table.setText(1, planNum+1, Integer.toString(premiums));
        table.setText(2, planNum+1, Integer.toString(prevOnly));
        table.setText(3, planNum+1, Integer.toString(seriousUse));
        table.setText(4, planNum+1, Integer.toString(customScenario));
    }
    
    @UiHandler("enterPlan")
    public void enterDataClicked(ClickEvent e) {
        presenter.enterDataClicked();
    }
}
