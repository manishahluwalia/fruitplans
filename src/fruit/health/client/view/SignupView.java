package fruit.health.client.view;

import com.google.gwt.user.client.ui.Widget;

import fruit.health.client.mvp.BaseView;
import fruit.health.client.view.SignupView.Presenter;
import fruit.health.shared.entities.User;

public interface SignupView extends BaseView<Presenter>
{
	public interface Presenter {
		public void onSubmitPressed();
        void onDataModified();
        void onLoginClicked();
        void onForgotClicked();
        void onPasswordModified(String password);
	}
	
	public void enableSubmit(boolean enable);
	
	public void setPresenter(Presenter presenter);
	public Widget asWidget();
    public void clear();
    public void setModel(User userDetails);

    public void showError(String string);
}
