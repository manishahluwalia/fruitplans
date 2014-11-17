package fruit.health.shared.dto;

import java.io.Serializable;

import fruit.health.server.cloner.api.Clone;
import fruit.health.server.cloner.api.ReflexivelyClonable;
import fruit.health.shared.entities.User;

@ReflexivelyClonable
public class LoginInfo implements Serializable
{
    public enum LoginMethod
    {
        PASSWORD, SIGNUP, REMEMBER_ME, PASSWORD_RESET_LINK, NEW_EMAIL_LINK, ADMIN_FAKE_LOGIN,
    }

    private static final long serialVersionUID = 1L;

    @Clone private User user;
    @Clone private LoginMethod loginMethod;
    private String sessionTracker;

    /**
     * If this is the case of an admin faking a real user, the name of the admin
     */
    private String admin;


    public void setUser (User userAccount)
    {
        this.user = userAccount;
    }

    public User getUser ()
    {
        return user;
    }

    public void setLoginMethod (LoginMethod loginMethod)
    {
        this.loginMethod = loginMethod;
    }

    public LoginMethod getLoginMethod ()
    {
        return loginMethod;
    }

    public void setSessionTracker (String sessionTracker)
    {
        this.sessionTracker = sessionTracker;
    }

    public String getSessionTracker ()
    {
        return sessionTracker;
    }

    public void setAdmin (String admin)
    {
        this.admin = admin;
    }

    public String getAdmin ()
    {
        return admin;
    }
}
