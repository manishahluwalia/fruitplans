package fruit.health.shared.dto;

import java.io.Serializable;

public class InitInfo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String googleAnalyticsId;
    private String commitId;
    private String visitId;

    /**
     * @return the googleAnalyticsId
     */
    public String getGoogleAnalyticsId ()
    {
        return googleAnalyticsId;
    }

    /**
     * @param googleAnalyticsId the googleAnalyticsId to set
     */
    public void setGoogleAnalyticsId (String googleAnalyticsId)
    {
        this.googleAnalyticsId = googleAnalyticsId;
    }

    /**
     * @return the commitId
     */
    public String getCommitId ()
    {
        return commitId;
    }

    /**
     * @param commitId the commitId to set
     */
    public void setCommitId (String commitId)
    {
        this.commitId = commitId;
    }

    /**
     * @return the visitId
     */
    public String getVisitId ()
    {
        return visitId;
    }

    /**
     * @param visitId the visitId to set
     */
    public void setVisitId (String visitId)
    {
        this.visitId = visitId;
    }
}
