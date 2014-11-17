package fruit.health.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;

import fruit.health.client.I18NConstants;
import fruit.health.client.gin.AppGinjector;

/**
 * According to the contract of {@link Place}, each place is supposed to provide
 * implementations of {@link Object#equals(Object)} and {@link Object#hashCode()}
 * This base class provides the implementation of these 2 functions, by requiring the
 * derived class to implement the {@link #getToken()} function.
 * <p/>
 * This also provides a {@link Object#toString()} implementation.
 */
public abstract class BasePlace extends Place
{
    protected static I18NConstants constants = null;
    
    public static void initialize(AppGinjector injector)
    {
        constants = injector.getI18NConstants();
    }
    
    protected boolean notReflexive = false;

    /**
	 * Return true iff the given object is an instance of
	 * this place type (not a subtype) and their tokens
	 * are <code>equal</code>
	 */
	@Override
	public boolean equals(Object _other) {
		if (null==_other) {
			return false;
		}
		/* Violation of http://www.artima.com/lejava/articles/equality.html,
		 * but we're not doing subclassing of places
		 */
		if (!this.getClass().equals(_other.getClass())) {
			return false;
		}
		
		if (this instanceof ReloadingPlace) {
		    // See the comment for ReloadingPlace
		    return false;
		}

		BasePlace other = ((BasePlace)_other);
		
		if (notReflexive || other.notReflexive) {
		    return false;
		}
		
		String myToken = this.getToken();
		String otherToken = other.getToken();
		if (null==myToken) {
			if (null==otherToken) {
				return true;
			} else {
				return false;
			}
		} else {
			return myToken.equals(otherToken);
		}
	}
	
	@Override
	public int hashCode() {
		String token = this.getToken();
		int h = this.getClass().hashCode();
		if (null!=token) {
			h += token.hashCode();
		}
		return h;
	}
	
    /**
     * This is a HUGE hack, but this is the only way I've found to make things
     * work.
     * <p/>
     * There are many ways in which a user can attempt to go to a place that is
     * an authenticated place when she is not logged in. Say:
     * <ol>
     * <li>Enters a URL manually / via bookmark / following a click from
     * somewhere</li>
     * <li>Clicks on a link on the app that takes her to an AutenticatedPlace</li>
     * <li>Logs out and hits the back button enough number of times</li>
     * </ol>
     * In all cases, GWT records a change of current place, however, we want to
     * redirect them to a login place first. This has the problem of changing
     * browser history (which is another item the user can bookmark or go "back"
     * to, creating further confusion).
     * <p/>
     * Instead, we let GWT take us to whichever place. When mapping places to
     * activities, we look for this case and create the {@link LoginActivity} as
     * opposed to the desired activity. When login is done, it knows to attempt
     * to take us back to the original place. At this point in time, GWT would
     * perform a check and see that its been sent to the place where its already
     * at and would throw away the place change request. It performs this check
     * via the {@link Object#equals(Object)} method. So what we do is that we
     * make this check return false in this case (a CLEAR violation of Java's
     * specification for the equals method). This fools GWT into attempting to
     * go to this place again. However, the browser does a textual comparision
     * of the URL and doesn't modify history.
     * <p/>
     * GWT however does get fooled and will attempt to map the place back to an
     * activity. This time, since we are logged in, we will not return a
     * {@link LoginActivity} but the regular desired activity.
     */
    public void makePlaceNonReflexive()
    {
        notReflexive = true;
    }
    
	/**
	 * A given {@link BasePlace} object should always return the same
	 * token string.
	 * @return The token string for this object (may be <code>null</code>)
	 */
	protected String getToken()
	{
	    return "";
	}

    /**
     * In some cases, we want the place name and ALL its internal parameters for
     * logging. This function provides that. It defaults to {@link #toString()}
     */
    public String toFullString ()
    {
        return toString();
    }
    
    @Override
    public String toString ()
    {
        return this.getClass().getSimpleName() + ":" + getToken();
    }
    
    /**
     * @param injector The injector to pass to the new activity instance
     * @return The activity to perform when we get to this place
     */
    public abstract Activity getActivity(AppGinjector injector);
    
    /**
     * The name of the place, in "noun" form.
     * @return
     */
    public abstract String getPlaceName();
    
    /**
     * The name of the place, in "verb" form. Or, the name of the action that
     * happens when we go to this place.
     * <p/>
     * Defaults to {@link #getPlaceName(AppGinjector)}
     * @return
     */
    public String getPlaceActionName() {
        return getPlaceName();
    }
}
