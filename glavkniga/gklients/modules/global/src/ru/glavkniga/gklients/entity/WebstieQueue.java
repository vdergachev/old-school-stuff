/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;
import javax.persistence.Column;

import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Updatable;
import java.util.Date;
import com.haulmont.cuba.core.entity.Creatable;
import com.haulmont.chile.core.annotations.Composition;
import ru.glavkniga.gklients.enumeration.WebstieOperations;

import java.util.Set;
import javax.persistence.OneToMany;

/**
 * @author LysovIA
 */
@Table(name = "GKLIENTS_WEBSTIE_QUEUE")
@Entity(name = "gklients$WebstieQueue")
public class WebstieQueue extends BaseUuidEntity implements Updatable, Creatable {
    private static final long serialVersionUID = -737478854084066713L;

    @Column(name = "ENTITY_ID")
    protected UUID entityId;

    @Column(name = "ENTITY_NAME")
    protected String entityName;

    @Column(name = "OPERATION")
    protected Integer operation;

    @Composition
    @OneToMany(mappedBy = "websiteQueueItem")
    protected Set<WebsiteQueueFilter> websiteQuieueFilter;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;


    public void setWebsiteQuieueFilter(Set<WebsiteQueueFilter> websiteQuieueFilter) {
        this.websiteQuieueFilter = websiteQuieueFilter;
    }

    public Set<WebsiteQueueFilter> getWebsiteQuieueFilter() {
        return websiteQuieueFilter;
    }


    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    @Override
    public Date getCreateTs() {
        return createTs;
    }


    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    @Override
    public Date getUpdateTs() {
        return updateTs;
    }


    public void setOperation(WebstieOperations operation) {
        this.operation = operation == null ? null : operation.getId();
    }

    public WebstieOperations getOperation() {
        return operation == null ? null : WebstieOperations.fromId(operation);
    }


    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }


}