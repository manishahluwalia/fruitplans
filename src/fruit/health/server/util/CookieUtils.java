
package fruit.health.server.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils
{
    /**
     * Extract the cookie of the specified name from the request. If there are
     * multiple cookies of the same name, only the first is returned.
     * 
     * @param request
     *            The request, which must not be null
     * @param cookieName
     *            The name of the cookie
     * @return The cookie, or null if no such cookie is found.
     */
    public static Cookie getCookie (HttpServletRequest request,
            String cookieName)
    {
        if (null == request.getCookies())
        {
            return null;
        }

        for (Cookie c : request.getCookies())
        {
            if (c.getName().equals(cookieName))
            {
                return c;
            }
        }
        return null;
    }

    public static void removeCookieByName (HttpServletResponse response,
            String cookieName)
    {
        Cookie c = new Cookie(cookieName, "");
        c.setMaxAge(0);
        c.setPath("/");
        response.addCookie(c);
    }

    public static void setCookie (HttpServletResponse response,
            String cookieName, String cookieValue)
    {
        setCookie(response, cookieName, cookieValue, -1);
    }

    public static void setPersistentCookie (HttpServletResponse response,
            String cookieName, String cookieValue)
    {
        setCookie(response, cookieName, cookieValue, 10 * 365 * 24 * 60 * 60); // 10
                                                                               // years
    }

    public static void setCookie (HttpServletResponse response,
            String cookieName, String cookieValue, int maxAge)
    {
        Cookie c = new Cookie(cookieName, cookieValue);
        c.setMaxAge(maxAge);
        c.setPath("/");
        response.addCookie(c);
    }

    // Disable instantiation
    private CookieUtils ()
    {}
}
