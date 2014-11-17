
package fruit.health.shared.entities;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import fruit.health.server.cloner.api.Clone;
import fruit.health.server.cloner.api.CopyFromClient;
import fruit.health.server.cloner.api.DoNotClone;
import fruit.health.server.cloner.api.ReflexivelyClonable;
import fruit.health.shared.dto.PasswordHashAlgo;



@NamedQueries({
    @NamedQuery(name = "findUserByEmail", query = "SELECT u FROM User u WHERE u.email= :email"),
    @NamedQuery(name = "findUserByVerifier", query = "SELECT u FROM User u WHERE u.verificationToken= :verificationToken")
})    
@Entity
@ReflexivelyClonable
public class User implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final int REMEMBER_ME_TOKEN_BYTES = 16;
    public static final int VERIFICATION_TOKEN_BYTES = 16;
    public static final int BROWSER_AUTH_TOKEN_BYTES = 16;

    @Clone
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @Clone
    @Column(nullable=false)
    @CopyFromClient
    private String email;

    /* Stuff needed to authenticate user */
    
    @Column(nullable=true, length=REMEMBER_ME_TOKEN_BYTES*2)
    private String rememberMeToken;
    
    @Column(nullable=true, length=VERIFICATION_TOKEN_BYTES*2)
    private String verificationToken; // Used for "forgot password" or "verify email"
    @Temporal(TemporalType.DATE)
    @Column (nullable=true)
    private Date verificationExpiration; // When does the verification token expire
    @Clone
    private boolean signedUp;
    
    @Column(nullable=true) @DoNotClone
    private PasswordHashAlgo pwdAlgo;
    @Column(nullable=true) @DoNotClone
    private String salt;
    @Column(nullable=true, length=64) @DoNotClone
    private String pwdHash;

    // User's personal details and preferences 
    @Clone
    @CopyFromClient
    private String fullName;
    
    @Clone
    @CopyFromClient
    private String locale;

    // For stats. TODO. Might make sense to keep these in a separate shadow entity, or make them not clonable
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date whenCreated;
    
    @Column(nullable=true)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date lastLogin;

    @Clone
    @Column(nullable=false)
    private boolean superAdmin;

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getRememberMeToken()
    {
        return rememberMeToken;
    }

    public void setRememberMeToken(String rememberMeToken)
    {
        this.rememberMeToken = rememberMeToken;
    }

    public String getVerificationToken()
    {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken)
    {
        this.verificationToken = verificationToken;
    }

    public Date getVerificationExpiration()
    {
        return verificationExpiration;
    }

    public void setVerificationExpiration(Date verificationExpiration)
    {
        this.verificationExpiration = verificationExpiration;
    }

    public boolean isSignedUp()
    {
        return signedUp;
    }

    public void setSignedUp(boolean signedUp)
    {
        this.signedUp = signedUp;
    }

    public PasswordHashAlgo getPwdAlgo()
    {
        return pwdAlgo;
    }

    public void setPwdAlgo(PasswordHashAlgo pwdAlgo)
    {
        this.pwdAlgo = pwdAlgo;
    }

    public String getSalt()
    {
        return salt;
    }

    public void setSalt(String salt)
    {
        this.salt = salt;
    }

    public String getPwdHash()
    {
        return pwdHash;
    }

    public void setPwdHash(String pwdHash)
    {
        this.pwdHash = pwdHash;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public Date getWhenCreated()
    {
        return whenCreated;
    }

    public void setWhenCreated(Date whenCreated)
    {
        this.whenCreated = whenCreated;
    }

    public Date getLastLogin()
    {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin)
    {
        this.lastLogin = lastLogin;
    }

    public boolean isSuperAdmin()
    {
        return superAdmin;
    }

    public void setSuperAdmin(boolean superAdmin)
    {
        this.superAdmin = superAdmin;
    }

}
