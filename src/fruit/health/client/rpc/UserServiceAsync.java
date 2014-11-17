package fruit.health.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fruit.health.shared.dto.LoginInfo;
import fruit.health.shared.entities.User;

public interface UserServiceAsync
{
    void login (String email, String password, boolean rememberMe, AsyncCallback<LoginInfo> callback);

    void registerUser (User newUserDetails, String password, AsyncCallback<LoginInfo> callback);

    void forgotPassword (String email, String urlBase, AsyncCallback<Void> callback);

    void resetPassword (String newPassword, String verifier, AsyncCallback<LoginInfo> callback);

    void changeEmailVerification (Long userId, String newEmail, String verifier, AsyncCallback<LoginInfo> callback);
}
