package fruit.health.client.view.desktop.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.Widget;

import fruit.health.client.view.SignupView;
import fruit.health.client.view.desktop.DesktopBrowserViewMaster;
import fruit.health.shared.entities.User;

public class SignupViewImpl extends Composite implements SignupView {

	private Presenter presenter = null;
	
	private static SignupViewUiBinder uiBinder = GWT
			.create(SignupViewUiBinder.class);

	@UiField HTMLPanel wrapper;
	@UiField DivElement error;
	@UiField InputElement email;
	@UiField InputElement name;
	@UiField InputElement password;

	@UiField SubmitButton submit;

    private User model;
	
	@UiTemplate("SignupView.ui.xml")
	interface SignupViewUiBinder extends UiBinder<Widget, SignupViewImpl> {
	}

	public SignupViewImpl(DesktopBrowserViewMaster desktopBrowserViewMaster) {
		initWidget(uiBinder.createAndBindUi(this));

        DOM.sinkEvents(email, Event.KEYEVENTS);
        DOM.setEventListener(email, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                model.setEmail(email.getValue());
                presenter.onDataModified();
            }
        });

        DOM.sinkEvents(name, Event.KEYEVENTS);
        DOM.setEventListener(name, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                model.setFullName(name.getValue());
                presenter.onDataModified();
            }
        });

        DOM.sinkEvents(password, Event.KEYEVENTS);
        DOM.setEventListener(password, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                presenter.onPasswordModified(password.getValue());
            }
        });
	}

	
	@Override
	public void clear() {
		email.setValue("");
		name.setValue("");
		password.setValue("");
		error.setInnerText("");
		error.setAttribute("hidden", "true");
		
		enableSubmit(false);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void enableSubmit(boolean enable) {
		submit.setEnabled(enable);
	}

    @Override
    public void setModel(User userDetails)
    {
        this.model = userDetails;
        email.setValue(model.getEmail());
        name.setValue(model.getFullName());
    }

    @UiHandler("submit")
    public void onSubmitPressed(ClickEvent e)
    {
        presenter.onSubmitPressed();
    }
    
    @Override
    public void showError(String errorMsg)
    {
        error.removeAttribute("hidden");
        error.setInnerText(errorMsg);
    }
}
