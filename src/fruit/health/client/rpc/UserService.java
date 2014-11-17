
package fruit.health.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fruit.health.shared.dto.LoginInfo;
import fruit.health.shared.entities.User;
import fruit.health.shared.exceptions.InvalidLoginCredsException;
import fruit.health.shared.exceptions.NoSuchEntityException;
import fruit.health.shared.exceptions.PasswordTooWeakException;
import fruit.health.shared.exceptions.UserExistsException;
import fruit.health.shared.exceptions.UserNotVerifiedException;
import fruit.health.shared.exceptions.VerificationExpiredException;
import fruit.health.shared.util.SharedConstants;

@RemoteServiceRelativePath(SharedConstants.GWT_SERVICE_PREFIX + "user/v0")
public interface UserService extends RemoteService
{
    /**
     * @param email
     * @param password
     * @param rememberMe true iff the user wants this browser instance to "remember" her
     * @return If properly logged in, the {@link LoginInfo} object.
     * @throws InvalidLoginCredsException If there was a problem logging the user in.
     * @throws UserNotVerifiedException If the user has signed up, but has not verified his email address
     * @throws AnonyUserAlreadyLinkedException If the anonymous user account has already been linked to a named user account previously and is being linked to a different user.
     */
    LoginInfo login (String email, String password, boolean rememberMe) throws InvalidLoginCredsException, UserNotVerifiedException;

    LoginInfo registerUser (User newUserDetails, String password) throws UserExistsException, InvalidLoginCredsException, PasswordTooWeakException;

    /**
     * @param The user with the given email forgot their password. Send them a recovery email with a verifier.
     * @param urlBase Upon successful completion, the routine will email the user a link to click. This provides
     *   the base of the url, which must include everything except the verification token. e.g.:
     *   http://127.0.0.1:8888/foo?q1=v1&q2=v2#!place:tokenPartA+
     *   The client should ensure that the url base is properly escaped as needed.
     *   The server will tack on a url-safe opaque string at the end that will be the verifier.
     * @throws NoSuchEntityException The email does not belong to a known user
     * @throws InvalidLoginCredsException The email is null
     */
    void forgotPassword (String email, String urlBase) throws InvalidLoginCredsException, NoSuchEntityException;

    /**
     * @param newPassword
     * @param verifier The verifier that was emailed to the user
     * @return If properly logged in, the {@link LoginInfo} object
     * @throws NoSuchEntityException The verifier is not valid (i.e. does not refer to a known user)
     * @throws InvalidLoginCredsException The verifier was null or ill formed; or the new password was null.
     * @throws VerificationExpiredException The user's verification token is no longer valid. User needs to ask to reset password again.
     */
    LoginInfo resetPassword (String newPassword, String verifier) throws InvalidLoginCredsException, NoSuchEntityException, VerificationExpiredException;

    /**
     * @param userId The id of the user
     * @param newEmail The new email address to change to
     * @param verifier The verifier that was emailed to the user
     * @return If properly logged in, the {@link LoginInfo} object
     * @throws NoSuchEntityException The old user id is not valid
     * @throws InvalidLoginCredsException The verifier is not valid
     * @throws VerificationExpiredException The verification token has expired. The user needs to log in again and ask for an email address change again.
     */
    LoginInfo changeEmailVerification (Long userId, String newEmail, String verifier) throws NoSuchEntityException, InvalidLoginCredsException, VerificationExpiredException;
}
