package fruit.health.server.endpoints.gwt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import logging.shared.RPCExceptionEncodingUtil;
import logging.shared.RemoteLoggingService;

import org.slf4j.LoggerFactory;

import com.google.gwt.core.server.StackTraceDeobfuscator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Singleton;

import fruit.health.server.util.Utils;
import fruit.health.shared.dto.LoginInfo;
import fruit.health.shared.util.SharedConstants;

@SuppressWarnings("serial")
@Singleton
public class RemoteLoggingServiceImpl extends RemoteServiceServlet implements
        RemoteLoggingService
{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RemoteLoggingServiceImpl.class);

    private final Logger julLogger;
    private final StackTraceDeobfuscator deobfuscator;
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss.SSS");

    public RemoteLoggingServiceImpl ()
    {
        julLogger = Logger.getLogger("logback2julProxy");

        StackTraceDeobfuscator stackTraceDeobfuscator = null;
        try
        {
            stackTraceDeobfuscator = StackTraceDeobfuscator.fromResource("WEB-INF/symbolMaps");
        }
        catch (Throwable t)
        {
            logger.warn("Can't get stackTraceDeobfuscator", t);
        }
        deobfuscator = stackTraceDeobfuscator;
    }

    public String logRecord (String keys, String requestHeaders,
            LogRecord record, byte[] encryptedServerSideInformation)
    {
        String message = record.getMessage();
        String logger = record.getLoggerName();

        final String logMessage;
        final Throwable exception;
        if (SharedConstants.CLIENT_TIMER_LOGGER_NAME.equals(logger))
        {
            exception = null;
            logMessage = "ClientTimer> @@ " + keys + " @@ "
                    + record.getMessage();
        }
        else if (SharedConstants.CLIENT_FLOW_LOGGER_NAME.equals(logger))
        {
            exception = null;
            logMessage = "ClientFlow> "
                    + formatter.format(new Date(record.getMillis())) + " @@ "
                    + keys + " @@ " + record.getMessage();
        }
        else
        {
            exception = record.getThrown();

            if (null != deobfuscator && null != exception)
            {
                /*
                 * See
                 * http://code.google.com/p/google-web-toolkit/wiki/WebModeExceptions
                 * In reality, this seems to be somewhat expensive to do on the
                 * production server. We should just dump and collect the logs
                 * as-is, and then process the stack trace with the help of the
                 * symbol files at our leisure on our own dime.
                 */
                deobfuscator.deobfuscateStackTrace(exception, getPermutationStrongName());
            }

            StringBuilder serverStack = new StringBuilder();
            if (null != encryptedServerSideInformation)
            {
                serverStack.append("\nCaused on the server by: ");
                serverStack.append(RPCExceptionEncodingUtil.decryptEncryptedServerSideInformation(encryptedServerSideInformation));
            }

            logMessage = "ClientDev> "
                    + formatter.format(new Date(record.getMillis())) + " "
                    + logger + " " + record.getLevel().getName() + " @@ "
                    + keys + " @@ " + requestHeaders + " - " + message
                    + serverStack.toString();
        }

        julLogger.logp(record.getLevel(),
                record.getSourceClassName(),
                record.getSourceMethodName(),
                logMessage,
                exception);

        return null;
    }

    @Override
    public String sendLogs (HashMap<String, String> context,
            LinkedList<logging.shared.LogRecord> buffer)
    {
        HttpServletRequest request = getThreadLocalRequest();

        StringBuilder keys = new StringBuilder();
        keys.append("IP=");
        keys.append(request.getRemoteAddr());

        HttpSession session = request.getSession(false);
        if (null != session)
        {
            LoginInfo creds = Utils.getCredsFromSession(session);
            if (null != creds)
            {
                keys.append(", session=");
                keys.append(creds.getSessionTracker());

                keys.append(", userId=");
                keys.append(creds.getUser().getUserId());
            }
        }

        for (Entry<String, String> contextEntry : context.entrySet())
        {
            keys.append(", " + contextEntry.getKey() + "=");
            keys.append(contextEntry.getValue());
        }

        String requestHeaders = Utils.dumpRequestMetadata(getThreadLocalRequest());
        for (logging.shared.LogRecord record : buffer)
        {
            String error = logRecord(keys.toString(),
                    requestHeaders,
                    record.getLogRecord(),
                    record.getEncryptedServerSideInformation());
            if (null != error)
            {
                return error;
            }
        }

        return null;
    }
}
