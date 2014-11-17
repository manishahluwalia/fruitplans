package fruit.health.server.auditlog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import fruit.health.shared.util.InlineMap;

@Singleton
public class AuditLogger
{
    private static final Logger auditLogger = LoggerFactory.getLogger("AuditLogger");
    private static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss.SSS");

    /**
     * Log a particular event.
     * 
     * @param event
     *            Must not be null
     * @param keysAndValues
     */
    public void log (AuditEvent event, InlineMap keysAndValues)
    {
        assert null != event;

        Date now = new Date();

        StringBuilder auditLogMsg = new StringBuilder();

        auditLogMsg.append(formatter.format(now)).append(" @@ ");

        Map<String, String> mdc = MDC.getCopyOfContextMap();
        String separator = "";
        if (null != mdc)
        {
            for (Entry<String, String> e : mdc.entrySet())
            {
                auditLogMsg.append(separator)
                        .append(e.getKey())
                        .append("=")
                        .append(e.getValue());
                separator = ", ";
            }
        }

        auditLogMsg.append(" @@ ").append(event.toString()).append(" ");

        if (null != keysAndValues)
        {
            for (Entry<String, Object> e : keysAndValues.entrySet())
            {
                String key = e.getKey();
                Object value = e.getValue();
                addKeyValue(auditLogMsg, key, value);
            }
        }

        auditLogger.info(auditLogMsg.toString());
    }

    /**
     * Log a particular event, which has no event-specific values.
     * 
     * @param event
     *            Must not be null.
     * @see #log(AuditEvent, InlineMap)
     */
    public void log (AuditEvent event)
    {
        log(event, null);
    }

    /**
     * @param auditLogMsg
     *            The audit message being built
     * @param key
     * @param value
     */
    private void addKeyValue (StringBuilder auditLogMsg, String key,
            Object value)
    {
        auditLogMsg.append(key).append("=");
        if (null == value)
        {
            auditLogMsg.append("null");
        }
        else
        {
            auditLogMsg.append("\"")
                    .append(StringEscapeUtils.escapeJava(value.toString()))
                    .append("\"");
        }
        auditLogMsg.append(" ");
    }
}
