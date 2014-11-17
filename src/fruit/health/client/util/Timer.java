
package fruit.health.client.util;

import java.util.Date;
import java.util.logging.Logger;

import fruit.health.shared.util.SharedConstants;

/**
 * A class that measures events on the client side and logs them for later
 * processing.
 */
public class Timer
{
    private static Logger timerLog = Logger.getLogger(SharedConstants.CLIENT_TIMER_LOGGER_NAME);

    private int start;
    private final TimedEvent event;
    private final String extraInfo;

    /**
     * Like {@link #Timer(TimedEvent, String)} with null extra info
     */
    public Timer (TimedEvent event)
    {
        this(event, null);
    }

    /**
     * Start the timer for the given event type.
     * 
     * @param event
     *            The given event type
     * @param extraInfo
     *            An arbitrary string (can be null) that can form a 2nd
     *            dimension when analysing event. An event sub-type if you will.
     *            Please make this easily parsable on the analytics server.
     *            Suggested that you use a \S+ pattern.
     */
    public Timer (TimedEvent event, String extraInfo)
    {
        assert null != event;

        this.event = event;
        start = getTime();
        this.extraInfo = extraInfo;
    }

    /**
     * End the timer, and log the time spent.
     */
    public void end ()
    {
        int end = getTime();
        log(event, extraInfo, start, end);
    }

    /**
     * Restart the timer
     */
    public void restart ()
    {
        start = getTime();
    }

    /**
     * Cancel the timer.
     */
    public void cancel ()
    {
        start = -1;
    }

    /**
     * Get the current time, to use in timing events.
     */
    public static int getTime ()
    {
        return (int)(new Date().getTime() % SharedConstants.TIME_MODULO_MS);
    }

    /**
     * Like {@link #log(TimedEvent, String, int, int)} with null extra info.
     */
    public static void log (TimedEvent event, int start, int end)
    {
        assert -1 != start : "Timer has already been cancelled or has expired!";

        log(event, null, start, end);
        start = -1;
    }

    /**
     * Given a start and end time for the given event type. Compute and log the
     * duration. Get the time via {@link #getTime()}
     * 
     * @param event
     *            The given event type
     * @param extraInfo
     *            An arbitrary string (can be null) that can form a 2nd
     *            dimension when analyzing event. An event sub-type if you will.
     *            Please make this easily parsable on the analytics server.
     *            Suggested that you use a \S+ pattern.
     */
    public static void log (TimedEvent event, String extraInfo, int start,
            int end)
    {
        assert null != event;

        timerLog.info(event + " " + extraInfo + " duration=" + (end - start));
    }
}
