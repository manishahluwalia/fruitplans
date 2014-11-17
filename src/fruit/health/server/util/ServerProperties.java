
package fruit.health.server.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerProperties
{
    private static final Logger logger = LoggerFactory.getLogger(ServerProperties.class);

    private static final String SERVER_PROPERTIES_FILENAME = "server.properties";

    private static final String SERVER_URL_BASE_PROPERTY_NAME = "server.url.base";
    private static final String DEFAULT_SERVER_URL_BASE = "http://127.0.0.1:8888/";

    private static final String serverUrlBase;
    static
    {
        SubstitutedPropertyFile props = new SubstitutedPropertyFile(SERVER_PROPERTIES_FILENAME);
        serverUrlBase = props.getProperty(SERVER_URL_BASE_PROPERTY_NAME,
                DEFAULT_SERVER_URL_BASE);
    }

    private static final String BUILD_INFO_PROPERTIES_FILENAME = "build.info.txt";
    private static final String GIT_COMMIT_ID_ABBREV_PROPERTY_NAME = "git.commit.id.abbrev";
    private static final String DEFAULT_GIT_COMMIT_ID_ABBREV = "local";

    private static final String commitId;
    static
    {
        String _commitId;
        try
        {
            SubstitutedPropertyFile props = new SubstitutedPropertyFile(BUILD_INFO_PROPERTIES_FILENAME);
            _commitId = props.getProperty(GIT_COMMIT_ID_ABBREV_PROPERTY_NAME);
        }
        catch (Throwable t)
        {
            _commitId = DEFAULT_GIT_COMMIT_ID_ABBREV;
        }
        commitId = _commitId;
    }

    /**
     * Given parts of a URL, constructs the full URL that points to a resource
     * on this running server
     *
     * @param path
     *            The part of the URL excluding the first / after host:port and
     *            not including any key-values or fragments. null if no path.
     *            e.g. path/to/image.png
     * @param keyValues
     *            The list of key value query arguments in the form
     *            key1=val1&key2=val2 (values must be url encoded; must not
     *            contain ? prefix). null if no query arguments
     * @param fragment
     *            The fragment part of the url. Must not include the #
     *            character. null if no fragment. e.g.
     *            fooPlace:tokenizedPlaceArg. Must be url encoded.
     * @return
     */
    public static String getInwardUrl (String path, String keyValues,
            String fragment)
    {
        return serverUrlBase + (null == path ? "" : path)
                + (null == keyValues ? "" : "?" + keyValues)
                + (null == fragment ? "" : "#!" + fragment);
    }

    /**
     * Generates a full URL that points to a GWT place for the app on this server
     * @param path See {@link #getInwardUrl(String, String, String)
     * @param keyValues See {@link #getInwardUrl(String, String, String)
     * @param placeName The name of the place. Should not be null. (Should not need to be url encoded.)
     * @param placeToken The non-url encoded token for the place.
     * @return
     */
    public static String getInwardUrl (String path, String keyValues,
            String placeName, String placeToken)
    {
        try
        {
            return getInwardUrl(path, keyValues, placeName + ":"
                        + (null == placeToken ? "" : URLEncoder.encode(placeToken, Constants.UTF8.name())));
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("Utf-8 not supported!!!", e);
            return null;
        }
    }

    public static String getCommitId ()
    {
        return commitId;
    }
}
