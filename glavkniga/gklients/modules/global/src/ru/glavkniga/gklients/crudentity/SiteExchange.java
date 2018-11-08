/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.crudentity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import java.util.Date;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;

/**
 * @author Хомяк
 */
@MetaClass(name = "gklients$SiteExchange")
public class SiteExchange extends AbstractNotPersistentEntity {
    private static final long serialVersionUID = -3395815316561561519L;

    @MetaProperty
    protected Integer siteId;

    @MetaProperty
    protected String tableName;

    @MetaProperty
    protected String data;

    @MetaProperty
    protected Date lastUpdate;

    @MetaProperty
    protected String status;

    @MetaProperty
    protected String event;

    public SiteExchangeStatus getStatus() {
        return status == null ? null : SiteExchangeStatus.fromId(status);
    }

    public void setStatus(SiteExchangeStatus status) {
        this.status = status == null ? null : status.getId();
    }


    public SiteExchangeEvent getEvent() {
        return event == null ? null : SiteExchangeEvent.fromId(event);
    }

    public void setEvent(SiteExchangeEvent event) {
        this.event = event == null ? null : event.getId();
    }



    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }


}