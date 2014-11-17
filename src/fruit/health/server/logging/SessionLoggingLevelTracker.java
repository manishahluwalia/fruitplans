package fruit.health.server.logging;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.Level;

public class SessionLoggingLevelTracker
{
    private static final Logger logger = LoggerFactory.getLogger(SessionLoggingLevelTracker.class);
    
    private static final String LOGGING_LEVEL_SESSION_KEY = "logging.threshold";
    private static final String LOGGING_LEVEL_MDC_KEY = "threshold";

    public static void setLevelForSession (String serverLoggingThreshold, HttpSession session)
    {
        if (null == serverLoggingThreshold)
        {
            session.removeAttribute(LOGGING_LEVEL_SESSION_KEY);
            MDC.remove(LOGGING_LEVEL_MDC_KEY);
            return;
        }

        // check if string is valid
        try
        {
            Level.toLevel(serverLoggingThreshold);
        }
        catch (Exception e)
        {
            logger.info("Ignoring invalid threshold: " + serverLoggingThreshold);
            return;
        }
        
        session.setAttribute(LOGGING_LEVEL_SESSION_KEY, serverLoggingThreshold);
        MDC.put(LOGGING_LEVEL_MDC_KEY, serverLoggingThreshold);
    }

    public static void setLevelForRequest (HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (null == session)
        {
            return;
        }

        Object threshold = session.getAttribute(LOGGING_LEVEL_SESSION_KEY);
        if (null==threshold)
        {
            logger.debug("Did not get a logging threshold in session");
            return;
        }
        if (!(threshold instanceof String))
        {
            // We only put in String. For now. Making this future proof
            // (e.g. an upgrade might put in other objects)
            logger.info("Ignoring a logging threshold in session of type " + threshold.getClass().getName());
            return;
        }

        MDC.put(LOGGING_LEVEL_MDC_KEY, (String) threshold);
    }

    /**
     * A new session has been created while processing this request. Restore the logging level
     * for the new session.
     * @param session
     */
    public static void restoreLoggingLevelForNewSession(HttpSession session)
    {
        String threshold = MDC.get(LOGGING_LEVEL_MDC_KEY);
        if (null!=threshold)
        {
            session.setAttribute(LOGGING_LEVEL_SESSION_KEY, threshold);
        }
    }

}
