package fruit.health.server.logging;

import org.slf4j.Logger;

import com.google.gson.Gson;

import fruit.health.server.util.Utils;

public class LoggingUtils
{
    /**
     * Given a string builder and an object, write out the object into the
     * {@link StringBuilder} in as much detail as possible. If the object is
     * <code>null</code>, it merely writes that out. Otherwise, the class name of the object is
     * written out followed by a deep recursive JSON serialization of its
     * contents. If the JSON serialization fails for some reason, then the
     * {@link Object#toString()} method is used as a fallback.
     * In we are in not development mode, we only dump the class name of the object,
     * not its contents.
     * <p/>
     * This function is expensive, so please only call it if you really need to.
     */
    public static void dumpObject (StringBuilder sb, Object o)
    {
        if (null == o)
        {
            sb.append("{null}");
            return;
        }

        sb.append(o.getClass().getName());

        if (!Utils.inDevelopmentMode())
        {
            sb.append("(no dumping objects contents in production mode)");
            return;
        }
        
        sb.append('{');
        Gson gson = new Gson(); // Lazy man's deep reflection and serialization
        try
        {
            sb.append(gson.toJson(o));
        }
        catch (Exception e)
        {
            sb.append(o.toString());
        }
        sb.append('}');
    }

    /**
     * This function will dump to the given logger in TRACE mode, the
     * ClassName.MethodName:LineNumber of the caller followed by a deep
     * recursive dump of the given return value (see
     * {@link #dumpObject(StringBuilder, Object)}). If the logger would not print
     * TRACEs, the function does nothing (i.e. this is efficient).
     * <p/>
     * 
     * Sample usage:
     * 
     * <pre>
     * Logger logger = LoggerFactory.getLogger(MyClass.class);
     * 
     * String someFunc ()
     * {
     *     return LoggingUtils.logAndReturn(logger, &quot;hello&quot;);
     * }
     * </pre>
     * 
     * This produces the output:
     * 
     * <pre>
     * Returning from my.package.MyClass.someFunc:38 with java.lang.String{"hello"}
     * </pre>
     * 
     * @param <T> The type of the returned value
     * @param logger The {@link Logger} to log to
     * @param retVal The actual return value
     * @return The given return value, for convenience.
     */
    public static <T> T logAndReturn (Logger logger, T retVal)
    {
        return logAndReturn(1, logger, retVal);
    }

    /**
     * A utility function that is used to make wrappers around
     * {@link #logAndReturn(Logger, Object)}
     * 
     * @param stackFramesToIgnore
     *            The <em>additional</em> number of stack frames to ignore.
     *            Typically, this will be the number of stack frames added to
     *            the call stack by the wrapper infrastructure.
     */
    public static <T> T logAndReturn (int stackFramesToIgnore, Logger logger,
            T retVal)
    {
        if (logger.isTraceEnabled())
        {
            StringBuilder sb = new StringBuilder("Returning from ");
            getFunctionNameAndLineNumber(sb, stackFramesToIgnore + 1);
            sb.append(" with ");
            dumpObject(sb, retVal);
            logger.trace(sb.toString());
        }
        return retVal;
    }

    /**
     * This function will dump to the given logger in TRACE mode, the
     * ClassName.MethodName:LineNumber of the caller. If the logger would not
     * print TRACEs, the function does nothing (i.e. this is efficient).
     * <p/>
     * 
     * Sample usage:
     * 
     * <pre>
     * Logger logger = LoggerFactory.getLogger(MyClass.class);
     * 
     * void someFunc (int arg)
     * {
     *     if (arg &lt; 0)
     *     {
     *         LoggingUtils.logExit(logger);
     *     }
     * }
     * </pre>
     * 
     * This produces the output:
     * 
     * <pre>
     * Leaving my.package.MyClass.someFunc:38
     * </pre>
     * 
     * @param logger The {@link Logger} to log to
     */
    public static void logExit (Logger logger)
    {
        logExit(1, logger);
    }

    /**
     * A utility function that is used to make wrappers around
     * {@link #logExit(Logger)}
     * 
     * @param stackFramesToIgnore
     *            The <em>additional</em> number of stack frames to ignore.
     *            Typically, this will be the number of stack frames added to
     *            the call stack by the wrapper infrastructure.
     */
    public static void logExit (int stackFramesToIgnore, Logger logger)
    {
        if (logger.isTraceEnabled())
        {
            StringBuilder sb = new StringBuilder("Leaving ");
            getFunctionNameAndLineNumber(sb, stackFramesToIgnore + 1);
            logger.trace(sb.toString());
        }
    }

    /**
     * This function will dump to the given logger in TRACE mode, the
     * ClassName.MethodName:LineNumber of the caller. If the logger would not
     * print TRACEs, the function does nothing (i.e. this is efficient).
     * <p/>
     * 
     * Sample usage:
     * 
     * <pre>
     * Logger logger = LoggerFactory.getLogger(MyClass.class);
     * 
     * void someFunc (int arg)
     * {
     *     LoggingUtils.logEntry(logger);
     *     // ...
     * }
     * </pre>
     * 
     * This produces the output:
     * 
     * <pre>
     * Entering my.package.MyClass.someFunc:38
     * </pre>
     * 
     * @param logger
     *            The {@link Logger} to log to
     */
    public static void logEntry (Logger logger)
    {
        logEntry(1, logger);
    }

    /**
     * A utility function that is used to make wrappers around
     * {@link #logEntry(Logger)}
     * 
     * @param stackFramesToIgnore
     *            The <em>additional</em> number of stack frames to ignore.
     *            Typically, this will be the number of stack frames added to
     *            the call stack by the wrapper infrastructure.
     */
    public static void logEntry (int stackFramesToIgnore, Logger logger)
    {
        if (logger.isTraceEnabled())
        {
            StringBuilder sb = new StringBuilder("Entering ");
            getFunctionNameAndLineNumber(sb, stackFramesToIgnore + 1);
            logger.trace(sb.toString());
        }
    }

    /**
     * Dump to the given string builder the stack frame at the top, ignoring the
     * specified number of frames. This number should not make any attempt to
     * account for the call graph of the callee, including the callee itself.
     */
    private static void getFunctionNameAndLineNumber (StringBuilder sb,
            int stackFramesToIgnore)
    {
        StackTraceElement st = Thread.currentThread().getStackTrace()[stackFramesToIgnore + 2]; // getStackTrace() & getFunctionNameAndLineNumber
        sb.append(st.getClassName() + "." + st.getMethodName() + ":"
                + st.getLineNumber());
    }
}
