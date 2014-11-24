package fruit.health.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fruit.health.shared.dto.InitInfo;
import fruit.health.shared.util.SharedConstants;

@RemoteServiceRelativePath(SharedConstants.GWT_SERVICE_PREFIX + "init/v0")
public interface InitService extends RemoteService
{
    /**
     * @param referer The document's referrer
     * @param serverLogging The serverLogging query parameter
     * @return
     */
    InitInfo initClient (String referer, String serverLogging);
}
