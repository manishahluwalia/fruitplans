package fruit.health.server.util;


public class GoogleAnalytics
{
    private static final String GOOGLE_ANALYTICS_PROPERTY_FILENAME = "google.analytics.properties";
    private static final String GOOGLE_ANALYTICS_PROERTY_NAME = "google.analytics.accountid";
    private static final String DEFAULT_GOOGLE_ANALYTICS_ACCOUNT_ID = "UA-56924748-1"; // XXX Debug maven build issues "UA-xxxxxxxx-1";
    
    private static final String googleAnalyticsAccountId;
    static
    {
        SubstitutedPropertyFile props = new SubstitutedPropertyFile(GOOGLE_ANALYTICS_PROPERTY_FILENAME);
        googleAnalyticsAccountId = props.getProperty(GOOGLE_ANALYTICS_PROERTY_NAME, DEFAULT_GOOGLE_ANALYTICS_ACCOUNT_ID);
    }
    
    public static String getAccountId()
    {
        return googleAnalyticsAccountId;
    }
}
