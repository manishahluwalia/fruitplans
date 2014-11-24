package fruit.health.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fruit.health.shared.dto.InitInfo;

public interface InitServiceAsync
{

    void initClient(String referer, String serverLogging,
            AsyncCallback<InitInfo> asyncCallback);

}
