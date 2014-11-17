package fruit.health.shared.util;

public interface SharedConstants
{

    public static final String BROWSER_COOKIE_NAME = "bcookie";

    public static final String VISIT_ID_HEADER = "X-Visit-Id";

    public static final String CLIENT_TIMER_LOGGER_NAME = "ClientTimerLog";
    public static final String CLIENT_FLOW_LOGGER_NAME = "ClientFlowLog";

    public static final String LOCALE_COOKIE_NAME = "UserChosenLocale";
    
    public static final String GWT_SERVICE_PREFIX = "gwt.rpc/";

    public static final String SESSION_ID_COOKIE_NAME = "JSESSIONID";
    public static final String CSRF_GUARD_HEADER = "X-CSRF-Guard-Header";

    /**
     * GWT does not do long from JSNI -> GWT Java. Since time values are in MS
     * from epoch, they can be > size of int. However, we don't really need all
     * the resolution. Thus, we only store the time since epoch modulo 10K secs.
     */
    public final static int TIME_MODULO_MS = 10000000;

    /**
     * This is the current version of the client-server pair protocol. This
     * needs to be bumped when the server decides it cannot talk to an old
     * client -- the client will then need to reload, possibly loosing data that
     * the user may have typed. This should be done VERY VERY sparingly. MOST
     * versioning can be done by the server supporting both the old and new
     * communication protocols. Only in EXTREMELY rare cases would this ever
     * need to be touched.
     */
    public static final int CURRENT_RPC_VERSION = 1;

    /**
     * The server sets this http header in the 500 class response when it determines that
     * the RPC call made was one that needed authentication, but was made from an
     * unauthenticated session.
     */
    public static final String AUTHENTICATION_NEEDED_HEADER = "X-Failed-Because-Authentication-Needed";

    
    // The following constants are for the url and parameter names for the GetDataFromTable call
    public static final String GET_DATA_URL = "/getDataFromTable";
    public static final String GET_DATA_TABLE_ID_PARAM = "table";
    public static final String GET_DATA_START_IDX_PARAM = "start";
    public static final String GET_DATA_NUM_PARAM = "num";
}
