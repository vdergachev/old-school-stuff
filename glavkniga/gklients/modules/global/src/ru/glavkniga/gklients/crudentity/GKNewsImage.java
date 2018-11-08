/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.crudentity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Creatable;
import java.util.Date;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author IgorLysov
 */
@NamePattern("%s|siteId")
@Table(name = "GKLIENTS_GK_NEWS_IMAGE")
@Entity(name = "gklients$GKNewsImage")
public class GKNewsImage extends BaseUuidEntity implements Creatable {
    private static final long serialVersionUID = 509766411783375345L;

    @Column(name = "SITE_ID")
    protected Integer siteId;

    @Column(name = "IMAGE")
    protected String image;

    @Column(name = "RECORD_ID")
    protected Integer recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GK_NEWS_ID")
    protected GKNews gkNews;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getRecordId() {
        return recordId;
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


    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setGkNews(GKNews gkNews) {
        this.gkNews = gkNews;
    }

    public GKNews getGkNews() {
        return gkNews;
    }


}