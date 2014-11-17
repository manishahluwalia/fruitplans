package fruit.health.client.view;

import com.google.gwt.user.client.ui.Widget;

import fruit.health.client.mvp.BaseView;

public interface LoginView extends BaseView<LoginView.Presenter>{
	public interface Presenter {
		public void onEmailEntered(String email);
		public void onPasswordEntered(String password);
		public void onLoginPressed();
		
		public void onSignupClicked();
        void onForgotPasswordClicked();
        void onRememberMeClicked(boolean rememberMe);
	}
	
	public Widget asWidget();

    public void setEmail(String email);

    public void clear();

    public void focusPassword();

    public void focusEmail();

    void enableSubmit(boolean enable);

    public void showError(String errorMsg);
}
