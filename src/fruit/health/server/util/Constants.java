
package fruit.health.server.util;

import java.nio.charset.Charset;

public class Constants
{
    public static final Charset UTF8 = Charset.forName("utf8");

    public static final String BROWSER_COOKIE_VERSION = "0";
    public static final int BCOOKIE_AGE = 10 * 365 * 24 * 60 * 60; // 10 years
    /**
     * The number of random bytes in the browser id cookie.
     */
    public static final int BROWSER_ID_BYTES = 16;

    public static final int RANDOM_ID_BYTES = 12;

    public static final String TEMPORARY_REMEMBER_ME_COOKIE_NAME = "TRM";

    /*
     * Number of seconds from unix epoch to fruitdb epoch (01May2014)
     */
    public static final Long DAY_ZERO_UNIX_TIMESTAMP =  1398902400L;

    public static final String SESSION_LOGIN_INFO_ATTRIBUTE = "login.info";

    public static final String REMEMBER_ME_COOKIE_NAME = "RememberMeId";

    public static final int BROWSER_AUTH_TOKEN_BYTES = 16;

    private Constants ()
    { }
}
