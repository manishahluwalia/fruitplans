package fruit.health.client.view.desktop.views;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
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
	@UiField VerticalPanel plans;
	@UiField Anchor compare;
	
	@Override
	public void showPlans(List<PlanData> plans) {
	    if (plans.size()==0) {
	        compare.setVisible(false);
	        this.plansPara.setAttribute("hidden", "hidden");
	    } else {
	        compare.setVisible(true);
            this.plansPara.removeAttribute("hidden");
            this.plans.clear();
            
            for (final PlanData p : plans) {
                Label label = new Label(p.planName);
                label.addClickHandler(new ClickHandler()
                {
                    @Override public void onClick(ClickEvent event)
                    {
                        presenter.onPlanClicked(p);
                    }
                });
                this.plans.add(label);
            }
	    }
	}
    
    @UiHandler("compare")
    public void compareClicked(ClickEvent e) {
        presenter.comparePlansClicked();
    }
    
    @UiHandler("enterPlan")
    public void enterDataClicked(ClickEvent e) {
        presenter.enterDataClicked();
    }
}
