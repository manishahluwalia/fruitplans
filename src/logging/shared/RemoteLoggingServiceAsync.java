
package logging.shared;

import java.util.HashMap;
import java.util.LinkedList;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RemoteLoggingServiceAsync
{
    /**
     * @param context
     * @param buffer
     * @param callback
     */
    void sendLogs (HashMap<String, String> context,
            LinkedList<LogRecord> buffer, AsyncCallback<String> callback);

}
