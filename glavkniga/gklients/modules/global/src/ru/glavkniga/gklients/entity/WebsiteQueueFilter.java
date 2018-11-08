/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Updatable;
import java.util.Date;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.Creatable;
import javax.persistence.Lob;
import com.haulmont.chile.core.annotations.Composition;
import java.util.Set;
import javax.persistence.OneToMany;

/**
 * @author LysovIA
 */
@Table(name = "GKLIENTS_WEBSITE_QUEUE_FILTER")
@Entity(name = "gklients$WebsiteQueueFilter")
public class WebsiteQueueFilter extends BaseUuidEntity implements Updatable, Creatable {
    private static final long serialVersionUID = 496246835699973647L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WEBSITE_QUEUE_ITEM_ID")
    protected WebstieQueue websiteQueueItem;

    @Lob
    @Column(name = "PARAM_NAME")
    protected String paramName;

    @Lob
    @Column(name = "VALUE_")
    protected String value;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;

    public WebstieQueue getWebsiteQueueItem() {
        return websiteQueueItem;
    }

    public void setWebsiteQueueItem(WebstieQueue websiteQueueItem) {
        this.websiteQueueItem = websiteQueueItem;
    }



    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
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



}