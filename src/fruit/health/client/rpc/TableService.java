package fruit.health.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fruit.health.shared.dto.PageMetaData;
import fruit.health.shared.dto.PagingEnvelope;
import fruit.health.shared.dto.UploadParams;
import fruit.health.shared.entities.Table;
import fruit.health.shared.exceptions.NoSuchEntityException;
import fruit.health.shared.util.Pair;
import fruit.health.shared.util.SharedConstants;

@RemoteServiceRelativePath(SharedConstants.GWT_SERVICE_PREFIX + "upload/v0")
public interface TableService extends RemoteService
{
    UploadParams getUploadParams();
    long startUpload(String description, String schema, String storageName);

    PagingEnvelope<Pair<Table, String>> getTables(PageMetaData<Pair<Table, String>> currentTables);
    PagingEnvelope<Pair<Table, String>> getPreviousTables(PageMetaData<Pair<Table,String>> previous);
    
    Table getTable(long tableId) throws NoSuchEntityException;
}
