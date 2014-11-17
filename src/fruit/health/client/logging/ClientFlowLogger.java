package fruit.health.client.logging;

import java.util.Map.Entry;
import java.util.logging.Logger;

import fruit.health.shared.util.InlineMap;
import fruit.health.shared.util.SharedConstants;

public class ClientFlowLogger
{
    private static final Logger flowLogger = Logger.getLogger(SharedConstants.CLIENT_FLOW_LOGGER_NAME);

    /**
     * Log a particular event.
     * 
     * @param event
     *            Must not be null
     * @param keysAndValues
     */
    public static void log (ClientFlowEvent event, InlineMap keysAndValues)
    {
        assert null != event;

        StringBuilder logMsg = new StringBuilder();

        logMsg.append(event.toString() + " ");

        if (null != keysAndValues)
        {
            for (Entry<String, Object> e : keysAndValues.entrySet())
            {
                String key = e.getKey();
                Object value = e.getValue();
                addKeyValue(logMsg, key, value);
            }
        }

        flowLogger.info(logMsg.toString());
    }

    /**
     * Log a particular event, which has no event-specific values.
     * 
     * @param event
     *            Must not be null.
     * @see #log(ClientFlowEvent, InlineMap)
     */
    public static void log (ClientFlowEvent event)
    {
        log(event, null);
    }

    /**
     * @param logMsg
     *            The message being built
     * @param key
     * @param value
     */
    private static void addKeyValue (StringBuilder logMsg, String key,
            Object value)
    {
        logMsg.append(key + "=");
        if (null == value)
        {
            logMsg.append("null");
        }
        else
        {
            logMsg.append("\"" + value.toString() + "\"");
        }
        logMsg.append(" ");
    }
}
