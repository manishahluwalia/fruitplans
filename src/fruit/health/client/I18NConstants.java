package fruit.health.client;

import javax.inject.Singleton;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;

@GenerateKeys("com.google.gwt.i18n.rebind.keygen.MD5KeyGenerator")
@Generate(format = { "com.google.gwt.i18n.rebind.format.PropertiesFormat" },
        locales = { "default" })
@Singleton
public interface I18NConstants extends Constants
{
    // page titles
    @DefaultStringValue("Pick1Plan")
    @Description("The prefix for the title of the browser window which runs the application")
    String getWindowTitlePrefix ();

    @DefaultStringValue("Login")
    @Description("The title of the browser when the user is in the login place")
    String getLoginPlaceTitle ();

    @DefaultStringValue("Verify Email")
    @Description("The title of the browser when the user is in the verifyemail place")
    String getVerifyEmailPlaceTitle ();

    @DefaultStringValue("Signup")
    @Description("The title of the browser when the user is in the signup place")
    String getSignupPlaceTitle ();

    @DefaultStringValue("Reset Password")
    @Description("The title of the browser when the user is in the resetpassword place")
    String getResetPasswordPlaceTitle ();

    @DefaultStringValue("Forgot Password")
    @Description("The title of the browser when the user is in the forgotpassword place")
    String getForgotPasswordPlaceTitle ();

    @DefaultStringValue("Welcome to Fruithealth")
    @Description("The title of the browser when the user is in the home place")
    String getHomePlaceTitle ();

    @DefaultStringValue("Enter Health Plan Data")
    @Description("The title of the browser when the user is in the enterPlan place")
    String getEnterPlanPlaceTitle ();

    @DefaultStringValue("Edit Custom Scenario")
    @Description("The title of the browser when the user is in the editScenario place")
    String getEditScenarioPlaceTitle ();

    @DefaultStringValue("Comparision of Plans")
    @Description("The title of the browser when the user is in the comparePlans place")
    String getComparePlansPlaceTitle ();

    /*
    @DefaultStringValue("")
    @Description("The title of the browser when the user is in the  place")
    String getPlaceTitle ();
    */
    
    
    // alerts, dialogs and error messages
    @DefaultStringValue("Internal Error")
    @Description("The title for the alert dialog displayed when an internal error occurs")
    String getInternalErrorDialogTitle ();

    @DefaultStringValue("Oops, an internal error has occurred on our side. A message has been logged for us to investigate. We apologize for any inconvenience")
    @Description("The text for the alert dialog displayed when an internal error occurs")
    String getInternalErrorDialogMessage ();

    @DefaultStringValue("Loading Error")
    @Description("The title for the alert dialog displayed when an error occurs while loading the app")
    String getModuleLoadingErrorTitle ();

    @DefaultStringValue("Oops, we have encountered an error while loading the website. We will try reloading the page for you. If the error persists, please try again later. We apologize for any inconvenience")
    @Description("The text for the alert dialog displayed when an error occurs while loading the app")
    String getModuleLoadingErrorBody ();

    @DefaultStringValue("Logged out?")
    @Description("The title of the dialog box that pops up when the user is performing an authenticated operation but has been logged out")
    String reloginNeededDialogTitle ();

    @DefaultStringValue("You have been logged out due to inactivity or may have logged yourself out in another browser tab.  To perform this action, you will need to log in again.")
    @Description("The message in the dialog box that pops up when the user is performing an authenticated operation but has been logged out")
    String reloginNeededDialogMessage ();

    @DefaultStringValue("Password recovery help is on its way!")
    @Description("The title of the dialog box that displays after a password recovery email is sent")
    String recoveryEmailSentTitle ();

    @DefaultStringValue("We have sent you an email with password change instructions.")
    @Description("The contents of the dialog box that displays after a password recovery email is sent")
    String recoveryEmailSentMessage ();

    @DefaultStringValue("User verification not complete")
    @Description("The title of the dialog box that displays when a login attempt fails because the email address is not yet verified")
    String UserNotVerifiedTitle ();

    @DefaultStringValue("We need to complete your registration process. Please check your inbox (or spam folder) and click on the link.")
    @Description("The content of the dialog box that displays when a login attempt fails because the email address is not yet verified")
    String UserNotVerifiedMessage ();

    @DefaultStringValue("Invalid email address or password")
    @Description("The error displayed when the user enters incorrect email or password during login")
    String InvalidLoginCreds();

}
