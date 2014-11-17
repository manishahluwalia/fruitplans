package fruit.health.client.view.desktop.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import fruit.health.client.view.LoginView;
import fruit.health.client.view.desktop.DesktopBrowserViewMaster;

public class LoginViewImpl extends Composite implements LoginView {

	private Presenter presenter = null;
	
	private static LoginViewUiBinder uiBinder = GWT
			.create(LoginViewUiBinder.class);

	@UiField HTMLPanel wrapper;
	
	@UiField DivElement errorDiv;
	@UiField SpanElement error;
	
	@UiField InputElement email;
	@UiField InputElement password;
	
	@UiField Button loginButton;
	
	@UiTemplate("LoginView.ui.xml")
	interface LoginViewUiBinder extends UiBinder<Widget, LoginViewImpl> {
	}

	public LoginViewImpl(DesktopBrowserViewMaster desktopBrowserViewMaster) {
		initWidget(uiBinder.createAndBindUi(this));

        DOM.sinkEvents(email, Event.KEYEVENTS);
        DOM.setEventListener(email, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                onEmailChange();
            }
        });

        DOM.sinkEvents(password, Event.KEYEVENTS);
        DOM.setEventListener(password, new EventListener()
        {
            @Override
            public void onBrowserEvent(Event event)
            {
                onPasswordChange();
            }
        });
	}

	public void onEmailChange() {
		presenter.onEmailEntered(email.getValue());
	}

	public void onPasswordChange() {
		presenter.onPasswordEntered(password.getValue());
	}
	
    @Override
	public void clear() {
		email.setValue("");
		password.setValue("");
		
		error.setInnerText("");
		errorDiv.setAttribute("hidden", "true");
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

    @Override
    public void setEmail(String email)
    {
        this.email.setValue(email);
    }

    @Override
    public void focusPassword()
    {
        this.password.focus();
    }

    @Override
    public void focusEmail()
    {
        this.email.focus();
    }
    
    @UiHandler("loginButton")
    public void onSubmit(ClickEvent e)
    {
        presenter.onLoginPressed();
    }
    
    @Override
    public void enableSubmit(boolean enable)
    {
        loginButton.setEnabled(enable);
    }

    @Override
    public void showError(String errorMsg)
    {
        error.setInnerText(errorMsg);
        errorDiv.removeAttribute("hidden");
    }
}
