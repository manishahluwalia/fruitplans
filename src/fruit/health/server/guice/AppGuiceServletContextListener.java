
package fruit.health.server.guice;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;

import fruit.health.server.util.Utils;

public class AppGuiceServletContextListener extends GuiceServletContextListener
{
    private static final Logger logger = LoggerFactory.getLogger(AppGuiceServletContextListener.class);

    private static Stage stage = null;

    private static class InjectorHolder
    {
        private static Injector injector = Guice.createInjector(stage,
                new AppServletModule());

        private static Injector getInjector ()
        {
            return injector;
        }
    }

    @Override
    public void contextInitialized (ServletContextEvent e)
    {
        if (Utils.inDevelopmentMode())
        {
            stage = Stage.DEVELOPMENT;
        }
        else
        {
            stage = Stage.PRODUCTION;
        }
        logger.info("Starting in stage: " + stage);

        super.contextInitialized(e);
    }

    @Override
    protected Injector getInjector ()
    {
        return getCreatedInjector();
    }

    public static Injector getCreatedInjector ()
    {
        return InjectorHolder.getInjector();
    }
}
