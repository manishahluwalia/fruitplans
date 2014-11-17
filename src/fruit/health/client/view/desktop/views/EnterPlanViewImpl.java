package fruit.health.client.view.desktop.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import fruit.health.client.entities.PlanData;
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

        DOM.sinkEvents(name, Event.KEYEVENTS);
        DOM.setEventListener(name, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                presenter.onNameChanged(name.getValue());
            }
        });

        DOM.sinkEvents(premium, Event.KEYEVENTS);
        DOM.setEventListener(premium, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                presenter.onPremiumChanged(premium.getValue());
            }
        });

        DOM.sinkEvents(deductible, Event.KEYEVENTS);
        DOM.setEventListener(deductible, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                presenter.onDeductibleChanged(deductible.getValue());
            }
        });

        DOM.sinkEvents(copay, Event.KEYEVENTS);
        DOM.setEventListener(copay, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                presenter.onCopayChanged(copay.getValue());
            }
        });

        DOM.sinkEvents(oopMax, Event.KEYEVENTS);
        DOM.setEventListener(oopMax, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                presenter.onOopMaxChanged(oopMax.getValue());
            }
        });
	}
    
	@UiField InputElement name;
	@UiField InputElement premium;
	@UiField InputElement deductible;
	@UiField InputElement copay;
	@UiField InputElement oopMax;
	
	@UiField Button done;
	@UiField Button addAnother;
	@UiField Button compare;
	
	/*
	@UiHandler("name")
	public void onNameChanged(ValueChangeEvent<String> e) {
	    presenter.onNameChanged(e.getValue());
	}
	
    @UiHandler("premium")
    public void onPremiumChanged(ValueChangeEvent<String> e) {
        presenter.onPremiumChanged(e.getValue());
    }
    
    @UiHandler("deductible")
    public void onDeductibleChanged(ValueChangeEvent<String> e) {
        presenter.onDeductibleChanged(e.getValue());
    }
    
    @UiHandler("copay")
    public void onCopayChanged(ValueChangeEvent<String> e) {
        presenter.onCopayChanged(e.getValue());
    }
    
    @UiHandler("oopMax")
    public void onOopMaxChanged(ValueChangeEvent<String> e) {
        presenter.onOopMaxChanged(e.getValue());
    }
    */
    
    @UiHandler("done")
    public void onDonePressed(ClickEvent e) {
        presenter.onDonePressed();
    }
    
    @UiHandler("addAnother")
    public void onAddAnotherPressed(ClickEvent e) {
        presenter.onAddPressed();
    }
    
    @UiHandler("compare")
    public void onPressed(ClickEvent e) {
        presenter.onComparePressed();
    }

    @Override
    public void showData(PlanData plan)
    {
        name.setValue(makeEmptyStrIfNull(plan.planName));
        premium.setValue(convertToStr(plan.premium));
        deductible.setValue(convertToStr(plan.deductible));
        copay.setValue(convertToStr(plan.copay));
        oopMax.setValue(convertToStr(plan.oopMax));
    }

    private String convertToStr(Integer val)
    {
        if (null==val) {
            return "";
        }
        return val.toString();
    }

    private String makeEmptyStrIfNull(String str)
    {
        if (null==str) {
            return "";
        }
        return str;
    }

    @Override
    public void enableButtons(boolean enabled)
    {
        done.setEnabled(enabled);
        addAnother.setEnabled(enabled);
        compare.setEnabled(enabled);
    }
}
