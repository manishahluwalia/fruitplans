package fruit.health.client.view;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import fruit.health.client.LocaleChooser;
import fruit.health.client.gin.AppGinjector;
import fruit.health.shared.entities.User;
import fruit.health.shared.util.RunnableWithArg;

/**
 * The "master" for the views. It provides a bunch of services specific to the
 * browser (category) we are running under:
 * <ul>
 * <li>Alert and confirm dialogs</li>
 * <li>The real, browser specific implementation for the different views</li>
 * <li>Initialization hooks</li>
 * <li>Hooks to set state of nav bar, login buttons</li>
 * </ul>
 */
public interface ViewMaster
{
    /**
     * The P of MVP. This is the presenter that controls the entire screen.
     * Contains callbacks for when screen elements are clicked or modified.
     */
    interface Presenter
    {
    }
    
	public interface ConfirmCallback<T> {
		/**
		 * @param ok true if ok was pressed. False if cancel was pressed.
		 * @param scope opaque object passed through
		 */
		public void onConfirm(boolean ok, T scope);
	}
	

    /**
     * Called one time at startup to setup things if needed.
     * 
     * @param presenter The presenter that controls the main screen
     * @param injector The injector
     * @return A "body" panel that accepts the widgets that will contain the
     *         main content
     */
    public AcceptsOneWidget initialize(Presenter presenter, AppGinjector injector);

	/**
	 * Display an alert dialog
	 * @param title The title of the dialog box
	 * @param msg The message text
	 * @param callback The callback when the user acknowledges the alert. May be null
	 * @param scope The opaque argument to the callback. May be null.
	 */
	public <T> void alertDialog(String title, String msg, RunnableWithArg<T> callback, T scope);
	
	/**
	 * Display a confirmation dialog
	 * @param title The title of the dialog box
	 * @param msg The message text
	 * @param callback The callback when the user acknowledges the dialog. May be null
	 * @param scope The opaque argument to the callback. May be null.
	 */
	public <T> void confirmDialog(String title, String msg, ConfirmCallback<T> callback, T scope);


    /**
     * Set the details for the logged in user (if any). Also, set the login / sigup buttons etc appropriately.
     * @param user null if there is no user logged in. A valid {@link User} for the logged in user otherwise
     */
    public void setLoggedInUser(User user);

    public void setLocaleChooser(LocaleChooser localeChooser);

    /**
     * @param busy If true, show the busy sign; if false, remove it
     */
    public void showBusy(boolean busy);
    
    public void showLoggedInMenu(boolean isAuthenticated);

    void setViewName(String viewName);

    /* Getters for the views */
    
    public void getEnterPlanView(RunnableWithArg<EnterPlanView> callback);
    public void getCompareView(RunnableWithArg<CompareView> callback);
}
