package fruit.health.client.view.desktop.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
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
	@UiField Anchor cancel;
	
    @UiHandler("done")
    public void onDonePressed(ClickEvent e) {
        presenter.onDonePressed();
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
        premium.setValue(InputValidation.convertToStr(plan.premium));
        deductible.setValue(InputValidation.convertToStr(plan.deductible));
        copay.setValue(InputValidation.convertToStr(plan.copay));
        oopMax.setValue(InputValidation.convertToStr(plan.oopMax));
    }

    @Override
    public void enableButtons(boolean enabled)
    {
        done.setEnabled(enabled);
        addAnother.setEnabled(enabled);
    }
}
