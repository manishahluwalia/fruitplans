package fruit.health.shared.entities;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;

import fruit.health.server.cloner.api.Clone;
import fruit.health.server.cloner.api.CopyFromClient;
import fruit.health.server.cloner.api.DoNotClone;
import fruit.health.server.cloner.api.ReflexivelyClonable;
import fruit.health.shared.dto.TableState;


@NamedQueries({
    @NamedQuery(name = "findTableByUser", query = "SELECT t FROM Table t WHERE t.userId= :userId"),
    @NamedQuery(name = "findAllTables", query = "SELECT t FROM Table t"),
    @NamedQuery(name = "findAllLoadingToDbTables", query = "SELECT t FROM Table t WHERE t.state=fruit.health.shared.dto.TableState.LOADING_TO_DB")
})    
@Entity
@ReflexivelyClonable
public class Table implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final int TABLE_ID_BYTES=16;

    @Id
    @Clone
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tableId;
    
    @Clone
    @Column(nullable=false)
    @CopyFromClient
    private String description;

    @Clone
    @Column(nullable=false)
    private String schema; // TODO: do this properly
    
    @Column(nullable=true, length=TABLE_ID_BYTES*2)
    @DoNotClone
    private String tableStorageName;
    
    // For stats. TODO. Might make sense to keep these in a separate shadow entity, or make them not clonable
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date whenCreated;

    @Clone
    @Column(nullable=false)
    private Long createdByUserId;

    @Clone
    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private TableState state;
    
    @Clone
    @Column
    private Long sizeInBytes; // null if unknown
    
    @Clone
    @Column
    private Long rows; // null if unknown
    
    @DoNotClone
    @Column(nullable=true)
    private String bigQueryLoadId;
    
    // More fields about who owns it, and who has what kind of access to it

    public Long getTableId()
    {
        return tableId;
    }

    public void setTableId(Long tableId)
    {
        this.tableId = tableId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getSchema()
    {
        return schema;
    }

    public void setSchema(String schema)
    {
        this.schema = schema;
    }

    public String getTableStorageName()
    {
        return tableStorageName;
    }

    public void setTableStorageName(String tableStorageName)
    {
        this.tableStorageName = tableStorageName;
    }

    public Date getWhenCreated()
    {
        return whenCreated;
    }

    public void setWhenCreated(Date whenCreated)
    {
        this.whenCreated = whenCreated;
    }

    public Long getCreatedByUserId()
    {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId)
    {
        this.createdByUserId = createdByUserId;
    }

    public TableState getState()
    {
        return state;
    }

    public void setState(TableState state)
    {
        this.state = state;
    }

    public Long getSizeInBytes()
    {
        return sizeInBytes;
    }

    public void setSizeInBytes(Long sizeInBytes)
    {
        this.sizeInBytes = sizeInBytes;
    }

    public Long getRows()
    {
        return rows;
    }

    public void setRows(Long rows)
    {
        this.rows = rows;
    }

    public String getBigQueryLoadId()
    {
        return bigQueryLoadId;
    }

    public void setBigQueryLoadId(String bigQueryLoadId)
    {
        this.bigQueryLoadId = bigQueryLoadId;
    }
}
