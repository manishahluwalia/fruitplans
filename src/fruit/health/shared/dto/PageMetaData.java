package fruit.health.shared.dto;
import java.io.Serializable;

import fruit.health.server.cloner.api.Clone;
import fruit.health.server.cloner.api.ReflexivelyClonable;


/**
 * When getting a large list of items from the server, it may not be
 * practical to get the entire list in one call. Which means that the
 * client may have to "page" through the list. This class holds the
 * meta-data for a "page" of data.
 * <p/>
 * Some of the fields here may be null. It is between the client and
 * the server to decide which fields are mandatory to be filled in and
 * which fields can be left null. i.e. how much effort needs to be put
 * into determining the value of the fields is out-of-scope for this
 * structure definition.
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
public class PageMetaData<T> implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    /**
     * If the we know the beginning index of the data in this "page".
     * null otherwise 
     */
    @Clone
    private Integer beginIndex;
    /**
     * The total number of items in the entire list (not just this page), if known.
     * null otherwise.
     */
    @Clone
    private Integer totalCount;
    /**
     * true if we know that there is more data after this page
     * false if we know that there is no more data after this page
     * null otherwise
     */
    @Clone
    private Boolean hasMoreAfter;
    /**
     * true if we know that there is more data before this page
     * false if we know that there is no more data before this page
     * null otherwise
     */
    @Clone
    private Boolean hasMoreBefore;
    /**
     * The "page number" for the current page, if it can be calculated.
     * Null otherwise.
     */
    @Clone
    private Integer pageNumber;
    /**
     * The total number of pages (including any partial pages), if known.
     * Null otherwise.
     */
    @Clone
    private Integer totalPages;
    /**
     * Space for the callee to store some state if needed. This can
     * help the callee to do the paging. Caller should treat this as
     * opaque. Callee should keep in mind that this will be visible to
     * caller.
     */
    @Clone
    private String calleeState;
    public Integer getBeginIndex ()
    {
        return beginIndex;
    }
    public void setBeginIndex (Integer beginIndex)
    {
        this.beginIndex = beginIndex;
    }
    public Integer getTotalCount ()
    {
        return totalCount;
    }
    public void setTotalCount (Integer totalCount)
    {
        this.totalCount = totalCount;
    }
    public Boolean getHasMoreAfter ()
    {
        return hasMoreAfter;
    }
    public void setHasMoreAfter (Boolean hasMoreAfter)
    {
        this.hasMoreAfter = hasMoreAfter;
    }
    public Boolean getHasMoreBefore ()
    {
        return hasMoreBefore;
    }
    public void setHasMoreBefore (Boolean hasMoreBefore)
    {
        this.hasMoreBefore = hasMoreBefore;
    }
    public Integer getPageNumber ()
    {
        return pageNumber;
    }
    public void setPageNumber (Integer pageNumber)
    {
        this.pageNumber = pageNumber;
    }
    public Integer getTotalPages ()
    {
        return totalPages;
    }
    public void setTotalPages (Integer totalPages)
    {
        this.totalPages = totalPages;
    }
    public void setCalleeState (String calleeState)
    {
        this.calleeState = calleeState;
    }
    public String getCalleeState ()
    {
        return calleeState;
    }
}
