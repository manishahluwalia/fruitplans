
package fruit.health.server.filters;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;

import fruit.health.server.bizlogic.RequestContextHolder;
import fruit.health.server.logging.SessionLoggingLevelTracker;
import fruit.health.server.util.Utils;

@Singleton
public class GeneralFilter implements Filter
{

    @Override
    public void destroy ()
    {}

    @Override
    public void doFilter (ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        Utils.clearCreds();
        SessionLoggingLevelTracker.setLevelForRequest(request);
        Utils.setupMdc(request, response);
        RequestContextHolder.setRequestResponse(request, response);
        try
        {
            chain.doFilter(request, response);
        }
        finally
        {
            Utils.restoreCredsIfDirtyAndClear((HttpServletRequest)request);
            RequestContextHolder.clear();
            MDC.clear();
        }
    }

    @Override
    public void init (FilterConfig arg0) throws ServletException
    {}

}
