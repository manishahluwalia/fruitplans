package fruit.health.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fruit.health.shared.dto.InitInfo;
import fruit.health.shared.dto.LoginInfo;
import fruit.health.shared.util.Pair;

public interface InitServiceAsync
{

    /**
     * @see InitService#initClient(String, String)
     */
    void initClient(String referrer, String serverLogging,
            AsyncCallback<Pair<InitInfo, LoginInfo>> asyncCallback);

}
