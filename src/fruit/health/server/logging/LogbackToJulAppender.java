package fruit.health.server.logging;

import java.util.logging.Logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class LogbackToJulAppender<E> extends UnsynchronizedAppenderBase<E>
{

    private Layout<E> layout;
    private final Logger julLogger;

    public LogbackToJulAppender ()
    {
        julLogger = Logger.getLogger("logback2julProxy");
    }

    @Override
    protected void append (E e)
    {
        if (!(e instanceof ILoggingEvent))
        {
            // We don't handle AccessEvents
            return;
        }
        String msg = layout.doLayout(e);
        ILoggingEvent le = (ILoggingEvent)e;
        java.util.logging.Level level = _getLevel(le.getLevel());
        StackTraceElement caller = le.getCallerData()[0];
        julLogger.logp(level,
                caller.getClassName(),
                caller.getMethodName(),
                msg);
    }

    private java.util.logging.Level _getLevel (
            ch.qos.logback.classic.Level level)
    {
        if (level.isGreaterOrEqual(ch.qos.logback.classic.Level.ERROR))
        {
            return java.util.logging.Level.SEVERE;
        }
        else if (level.isGreaterOrEqual(ch.qos.logback.classic.Level.WARN))
        {
            return java.util.logging.Level.WARNING;
        }
        else if (level.isGreaterOrEqual(ch.qos.logback.classic.Level.INFO))
        {
            return java.util.logging.Level.INFO;
        }
        else if (level.isGreaterOrEqual(ch.qos.logback.classic.Level.DEBUG))
        {
            return java.util.logging.Level.FINE;
        }
        else
        {
            return java.util.logging.Level.FINEST;
        }
    }

    public void setLayout (Layout<E> layout)
    {
        this.layout = layout;
    }

    public Layout<E> getLayout ()
    {
        return layout;
    }
}
