package fruit.health.client.view.desktop.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import fruit.health.client.entities.PlanData;
import fruit.health.client.util.InputValidation;
import fruit.health.client.view.EnterPlanView;
import fruit.health.client.view.EnterPlanView.Presenter;
import fruit.health.client.view.desktop.BaseViewImpl;
import fruit.health.client.view.desktop.DesktopBrowserViewMaster;

public class EnterPlanViewImpl extends BaseViewImpl<Presenter> implements EnterPlanView {
	
	private static EnterDataViewUiBinder uiBinder = GWT
			.create(EnterDataViewUiBinder.class);
	
	@UiTemplate("EnterPlanView.ui.xml")
	interface EnterDataViewUiBinder extends UiBinder<Widget, EnterPlanViewImpl> {
	}

	public EnterPlanViewImpl(DesktopBrowserViewMaster desktopBrowserViewMaster) {
		initWidget(uiBinder.createAndBindUi(this));

		name.getElement().setAttribute("placeholder", "What's the name of the plan (e.g. Kaiser, Blue Cross)");
		premium.getElement().setAttribute("placeholder", "How much your employer deducts per month");
        deductible.getElement().setAttribute("placeholder", "What is the annual deductible");
        copay.getElement().setAttribute("placeholder", "What is the % copay after you meet the deductible");
        oopMax.getElement().setAttribute("placeholder", "What is the annual out-of-pocket max");
	}
    
	@UiField TextBox name;
	@UiField IntegerBox premium;
	@UiField IntegerBox deductible;
	@UiField DoubleBox copay;
	@UiField IntegerBox oopMax;
	
	@UiField Button done;
	@UiField Button addAnother;
	@UiField Anchor cancel;
	
	@UiHandler("name")
	public void onNameChanged(ValueChangeEvent<String> e) {
	    presenter.onNameChanged(e.getValue());
	}
    
    @UiHandler("premium")
    public void onPremiumChanged(ValueChangeEvent<Integer> e) {
        presenter.onPremiumChanged(e.getValue());
    }
    
    @UiHandler("deductible")
    public void onDeductibleChanged(ValueChangeEvent<Integer> e) {
        presenter.onDeductibleChanged(e.getValue());
    }
    
    @UiHandler("copay")
    public void onCopayChanged(ValueChangeEvent<Double> e) {
        presenter.onCopayChanged(e.getValue());
    }
    
    @UiHandler("oopMax")
    public void onOopMaxChanged(ValueChangeEvent<Integer> e) {
        presenter.onOopMaxChanged(e.getValue());
    }
	
    @UiHandler("done")
    public void onDonePressed(ClickEvent e) {
        presenter.onComparePressed();
    }
    
    @UiHandler("addAnother")
    public void onAddAnotherPressed(ClickEvent e) {
        presenter.onAddPressed();
    }
    
    @UiHandler("cancel")
    public void onPressed(ClickEvent e) {
        presenter.onCancelPressed();
    }

    @Override
    public void showData(PlanData plan)
    {
        name.setValue(InputValidation.makeEmptyStrIfNull(plan.planName));
        premium.setValue(plan.premium);
        deductible.setValue(plan.deductible);
        copay.setValue(plan.copay);
        oopMax.setValue(plan.oopMax);
    }

    @Override
    public void enableButtons(boolean enabled)
    {
        done.setEnabled(enabled);
        addAnother.setEnabled(enabled);
    }
}
