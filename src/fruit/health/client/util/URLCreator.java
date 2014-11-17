
package fruit.health.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BasePlace;
import fruit.health.shared.util.SharedUtils;

@Singleton
public class URLCreator
{
    protected static final Logger logger = Logger.getLogger(URLCreator.class.getName());

    private final PlaceHistoryMapper placeHistoryMapper;

    @Inject
    public URLCreator (AppGinjector injector)
    {
        this.placeHistoryMapper = injector.getPlaceHistoryMapper();
    }

    /**
     * Given a place, creates an externally accessible URL that takes us there.
     * 
     * @param place The place. Cannot be null.
     * @return The URL.toString() of the URL to this place and token.
     * @throws IllegalArgumentException If the place is null or empty
     */
    public String getLinkToPlace (BasePlace place)
    {
        if (null == place)
        {
            throw new IllegalArgumentException();
        }

        String retVal = makeURL(Window.Location.getProtocol(),
                Window.Location.getHost(),
                Window.Location.getPath(),
                placeHistoryMapper.getToken(place),
                Window.Location.getParameterMap());

        logger.fine("creating link to place: '" + retVal + "'");

        return retVal;
    }

    /**
     * Given the parts, constructs a URL out of them.
     * 
     * @param protocol
     *            The name of the protocol, optionally including the ':' e.g.
     *            <code>http:</code>
     * @param host
     *            The host IP or name and port number (if any) e.g.
     *            <code>www.amazon.com</code> or <code>127.0.0.1:8888</code>
     * @param path
     *            The path, including servlet context, optionally starting with
     *            '/' e.g. <code>/MyServlet/path/to/resource</code>. Url
     *            encoded, if needed.
     * @param hash
     *            The fragment part of the URL including the hash character, or
     *            the empty string/null e.g. <code>#index</code> Url encoded, if
     *            needed.
     * @param params
     *            A map of parameters (keys, list of values). The keys should be
     *            urlencoded if needed. The values should not be urlencoded. can
     *            be null.
     * @param moreParams
     *            Any additional parameters to be added, each is of the form
     *            <code>name=urlEncodedValue</code>
     * @return The URL
     * @throws IllegalArgumentException
     *             If any of the input arguments is invalid (e.g. null or empty)
     */
    /*
     * The rules for the validity of the parameters above look a little weird.
     * (e.g. some need to be urlencoded, some should not be; protocol must
     * contain terminating :, but not //). This is because the rules are matched
     * to what GWT returns (e.g. Window.Location.getProtocol()) to make life
     * easy for most callers.
     */
    public String makeURL (String protocol, String host, String path,
            String hash, Map<String, List<String>> params, String... moreParams)
    {
        if (SharedUtils.isEmpty(protocol) || SharedUtils.isEmpty(host) || null == path)
        {
            throw new IllegalArgumentException();
        }

        UrlBuilder urlBuilder = new UrlBuilder();

        urlBuilder.setProtocol(protocol);
        urlBuilder.setHost(host);
        urlBuilder.setPath(path);
        urlBuilder.setHash(hash);

        if (null != params)
        {
            for (Entry<String, List<String>> param : params.entrySet())
            {
                if (!"gwt.codesvr".equals(param.getKey()))
                {
                    /*
                     * Only gwt.codesvr is currently passed thru. This one does
                     * need to be passed thru in order for dev-mode to work.
                     */
                    continue;
                }

                ArrayList<String> values = new ArrayList<String>();
                for (String value : param.getValue())
                {
                    values.add(value);
                }
                urlBuilder.setParameter(param.getKey(),
                        values.toArray(new String[0]));
            }
        }

        for (String param : moreParams)
        {
            // split the parameter at the '=' sign
            String[] elems = param.split("=", 2);

            if (2 > elems.length)
            {
                logger.warning("eliding invalid query parameter: '" + param
                        + "'");
                continue;
            }

            urlBuilder.setParameter(elems[0], elems[1]);
        }

        return urlBuilder.buildString();
    }

}
