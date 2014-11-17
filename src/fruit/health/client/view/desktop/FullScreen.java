package fruit.health.client.view.desktop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import fruit.health.client.gin.AppGinjector;
import fruit.health.client.view.ViewMaster.Presenter;

/**
 * This is the UI Widget that occupies the entire screen. It breaks the screen up into
 * the "main body", the top bar and the side bar.
 * 
 * The top bar contains the Logo / Login/Logout section / Change language section
 * @author Manish
 *
 */
public class FullScreen extends Composite {

	private static FullScreenUiBinder uiBinder = GWT
			.create(FullScreenUiBinder.class);

	@UiField HTMLPanel wholeScreen;
	@UiField DivElement busyOverlay;
	@UiField
	SimplePanel body;
	
	// Login / Action menu stuff
	@UiField DivElement isNotAuthenticated;
	
    @UiField HeadingElement viewName;
    
	private final Presenter presenter;
	
	@UiTemplate("FullScreen.ui.xml")
	interface FullScreenUiBinder extends UiBinder<Widget, FullScreen> {}

	public FullScreen(Presenter presenter, AppGinjector injector) {
		this.presenter = presenter;
		
		// Initialize ui
		initWidget(uiBinder.createAndBindUi(this));
		wholeScreen.getElement().setId("ng-view-wrap");
	
		wholeScreen.setStyleName("overflow_auto");
		
		showLoggedInMenu(false);
	}

	public void showBusy(boolean busy)
    {
	    if (!busy)
	    {
	        busyOverlay.setAttribute("hidden", "true");
	    }
	    else
	    {
	        busyOverlay.removeAttribute("hidden");
	    }
    }

    public AcceptsOneWidget getBodyPanel() {
		return body;
	}
	
	public void showLoggedInMenu(boolean isAuthenticated) {
	    if (!isAuthenticated)
	    {
	        // this.isAuthenticated.setAttribute("hidden", "true");
	        this.isNotAuthenticated.removeAttribute("hidden");
	    }
	    else
	    {
	        throw new RuntimeException("Not expecting to be authenticated");
	        //this.isNotAuthenticated.setAttribute("hidden", "true");
            //this.isAuthenticated.removeAttribute("hidden");
	    }
	}

	public void setViewName(String viewName)
	{
	    this.viewName.setInnerText(viewName);
	}
	
	@UiHandler({"logo1", "logo2", "logo3"})
	public void onLogoClicked(ClickEvent e)
	{
	    presenter.onLogoClicked();
	}
}
