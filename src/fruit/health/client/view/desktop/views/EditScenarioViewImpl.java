package fruit.health.client.view.desktop.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import fruit.health.client.entities.Scenario;
import fruit.health.client.util.InputValidation;
import fruit.health.client.view.EditScenarioView;
import fruit.health.client.view.EditScenarioView.Presenter;
import fruit.health.client.view.desktop.BaseViewImpl;
import fruit.health.client.view.desktop.DesktopBrowserViewMaster;

public class EditScenarioViewImpl extends BaseViewImpl<Presenter> implements EditScenarioView {
	
	private static EditScenarioViewUiBinder uiBinder = GWT
			.create(EditScenarioViewUiBinder.class);
	
	@UiTemplate("EditScenarioView.ui.xml")
	interface EditScenarioViewUiBinder extends UiBinder<Widget, EditScenarioViewImpl> {
	}

	public EditScenarioViewImpl(DesktopBrowserViewMaster desktopBrowserViewMaster) {
		initWidget(uiBinder.createAndBindUi(this));

        DOM.sinkEvents(age, Event.KEYEVENTS);
        DOM.setEventListener(age, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                presenter.onAgeChanged(age.getValue());
            }
        });

        DOM.sinkEvents(numDocVists, Event.KEYEVENTS);
        DOM.setEventListener(numDocVists, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                presenter.onNumDocVistsChanged(numDocVists.getValue());
            }
        });

        DOM.sinkEvents(numRxs, Event.KEYEVENTS);
        DOM.setEventListener(numRxs, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                presenter.onNumRxsChanged(numRxs.getValue());
            }
        });

        DOM.sinkEvents(numHospiDays, Event.KEYEVENTS);
        DOM.setEventListener(numHospiDays, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                presenter.onNumHospiDaysChanged(numHospiDays.getValue());
            }
        });
	}
    
	@UiField ListBox gender;
	@UiField InputElement age;
	@UiField InputElement numDocVists;
	@UiField InputElement numRxs;
	@UiField InputElement numHospiDays;
	@UiField Button done;
	
    @UiHandler("done")
    public void onDonePressed(ClickEvent e) {
        presenter.onDonePressed();
    }

    @UiHandler("gender")
    public void onGenderChange(ChangeEvent e) {
        presenter.onGenderChanged(1==gender.getSelectedIndex());
    }
    
    @Override
    public void showData(Scenario scenario)
    {
        if (scenario.isFemale) {
            gender.setSelectedIndex(1);
        } else {
            gender.setSelectedIndex(0);
        }
        age.setValue(InputValidation.convertToStr(scenario.age));
        numDocVists.setValue(InputValidation.convertToStr(scenario.numDocVisits));
        numRxs.setValue(InputValidation.convertToStr(scenario.numRxs));
        numHospiDays.setValue(InputValidation.convertToStr(scenario.numHospitalizations));
    }

    @Override
    public void enableButtons(boolean enable)
    {
        done.setEnabled(enable);
    }
}
