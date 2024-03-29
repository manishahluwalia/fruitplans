
package fruit.health.server.guice;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import logging.server.NonEncryptingEncoderDecoder;
import logging.shared.RPCExceptionEncodingUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.servlet.ServletModule;

import fruit.health.server.endpoints.gwt.InitServiceImpl;
import fruit.health.server.endpoints.gwt.RemoteLoggingServiceImpl;
import fruit.health.server.filters.GeneralFilter;

public class AppServletModule extends ServletModule
{
    final static Set<Class<? extends RemoteServiceServlet>> gwtEndpoints = new HashSet<Class<? extends RemoteServiceServlet>>(Arrays.asList(
            InitServiceImpl.class,
            RemoteLoggingServiceImpl.class
        ));

    /*
     * Don't modify anything below this line unless
     * you want to modify the functioning of the class
     * itself
     */

    private final static Logger logger = LoggerFactory.getLogger(AppServletModule.class);

    public static final String GWT_PREFIX = "/fruithealth/";

    private boolean foundErrors = false;

    @Override
    protected void configureServlets ()
    {
        /*
         * Make all requests go through the filter that sets MDC,
         * clears/restores creds, sets logging level, etc.
         */
        filter("/*").through(GeneralFilter.class);

        setupGwtRpc();

        if (foundErrors)
        {
            throw new RuntimeException();
        }
    }

    private void setupGwtRpc ()
    {
        // For client side logging:
        // Note, this opens us to a DOS attack by overflowing logs
        RPCExceptionEncodingUtil.setEncrypterDecrypter(new NonEncryptingEncoderDecoder());

        for (Class<? extends RemoteServiceServlet> c : gwtEndpoints)
        {
            logger.debug("Binding GWT endpoint: " + c.getName());
            serveGwtRpcServlet(c);
        }
    }

    private void serveGwtRpcServlet (Class<? extends RemoteServiceServlet> servletImpl)
    {
        boolean served = false;
        for (Class<?> iface : servletImpl.getInterfaces())
        {
            if (RemoteService.class.isAssignableFrom(iface))
            {
                RemoteServiceRelativePath annotation = iface.getAnnotation(RemoteServiceRelativePath.class);
                if (null == annotation)
                {
                    continue;
                }

                String path = annotation.value();
                if (null == path)
                {
                    continue;
                }

                logger.debug("Serving " + servletImpl.getName() + " at " + GWT_PREFIX + path);
                serve(GWT_PREFIX + path).with(servletImpl);
                served = true;
                break;
            }
        }

        if (!served)
        {
            logger.error(servletImpl.getName() + " not served; no @RemoteServiceRelativePath annotation found");
            foundErrors = true;
        }
    }
}
