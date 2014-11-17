
package logging.shared;

import java.util.HashMap;
import java.util.LinkedList;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("remote_logging")
public interface RemoteLoggingService extends RemoteService
{
    String sendLogs (HashMap<String, String> context,
            LinkedList<logging.shared.LogRecord> buffer);
}
