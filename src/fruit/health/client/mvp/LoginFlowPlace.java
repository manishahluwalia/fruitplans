package fruit.health.client.mvp;

import fruit.health.client.LoginStateManager;

/**
 * Marker interface. This is used by the {@link LoginStateManager} when a
 * not-logged in user decides to try something that needs authentication.
 * This can start a whole flow where a user is redirected to a login page,
 * and user clicks on signup page and then clicks on a verification link
 * etc. Finally, the user is sent to the place she wanted to go.
 * 
 * This interface marks these pages in the middle (or, to be more precise,
 * marks the places that these pages correspond to) because they
 * need to be treated differently by the {@link LoginStateManager}.
 */
public interface LoginFlowPlace {
}
