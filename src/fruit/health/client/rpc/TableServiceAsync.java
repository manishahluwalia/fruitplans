package fruit.health.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fruit.health.shared.dto.PageMetaData;
import fruit.health.shared.dto.PagingEnvelope;
import fruit.health.shared.dto.UploadParams;
import fruit.health.shared.entities.Table;
import fruit.health.shared.util.Pair;

public interface TableServiceAsync
{
    void getUploadParams(AsyncCallback<UploadParams> callback);

    void getTables(PageMetaData<Pair<Table, String>> currentTables,
            AsyncCallback<PagingEnvelope<Pair<Table, String>>> callback);

    void getPreviousTables(PageMetaData<Table> previous,
            AsyncCallback<PagingEnvelope<Pair<Table, String>>> callback);

    void startUpload(String description, String schema, String storageName,
            AsyncCallback<Long> callback);

    void getTable(long tableId, AsyncCallback<Table> callback);
}
