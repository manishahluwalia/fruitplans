package fruit.health.shared.dto;
import java.io.Serializable;
import java.util.LinkedList;

import fruit.health.server.cloner.api.Clone;
import fruit.health.server.cloner.api.ReflexivelyClonable;


/**
 * When getting a large list of items from the server, it may not be
 * practical to get the entire list in one call. Which means that the
 * client may have to "page" through the list. This envelope is meant
 * to provide a common scheme for the client and server to communicate
 * and page through this list.
 * <p/>
 * An envelope contains the data and a metaData structure. To get more
 * items from the list, pass the previous metaData structure back to
 * the server along with any additional request parameters.
 * <p/>
 * There may be more data "before" and "after" this page. The ordering
 * of items is also determined by contract between caller and callee.
 * <p/>
 * To use this, define calls between caller and callee like the
 * following:
 * <pre>
 * PagingEnvelope&lt;Post&gt; getLatestPosts();
 * PagingEnvelope&lt;Post&gt; getOlderPosts(PageMetaData&lt;Post&gt; currentPage);
 * </pre>
 * 
 * @param T The type of the list being paged through
 */
@ReflexivelyClonable
public class PagingEnvelope<T> implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    /**
     * The actual data contained in this page. Order is determined
     * by contract between caller and callee.
     * <p/>
     * Cannot be null. Can be the empty list.
     */
    @Clone
    private LinkedList<T> data;
    
    /**
     * The meta-data for this page. Cannot be null.
     */
    @Clone
    private PageMetaData<T> metaData;

    public LinkedList<T> getData ()
    {
        return data;
    }
    public void setData (LinkedList<T> data)
    {
        this.data = data;
    }
    public void setMetaData (PageMetaData<T> metaData)
    {
        this.metaData = metaData;
    }
    public PageMetaData<T> getMetaData ()
    {
        return metaData;
    }
}
