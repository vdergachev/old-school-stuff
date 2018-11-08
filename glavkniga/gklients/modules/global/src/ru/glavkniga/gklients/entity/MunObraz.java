/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import ru.glavkniga.gklients.enumeration.Region;

/**
 * @author LysovIA
 */
@NamePattern("%s|title")
@Table(name = "GKLIENTS_MUN_OBRAZ")
@Entity(name = "gklients$MunObraz")
public class MunObraz extends StandardEntity {
    private static final long serialVersionUID = -2236404782545360758L;

    @Column(name = "OKTMO", length = 20)
    protected String oktmo;

    @Column(name = "TITLE")
    protected String title;

    @Column(name = "CAPITAL")
    protected String capital;

    @Column(name = "REGION")
    protected String region;

    @Column(name = "OSM_ID")
    protected String osmId;


    public void setRegion(Region region) {
        this.region = region == null ? null : region.getId();
    }

    public Region getRegion() {
        return region == null ? null : Region.fromId(region);
    }


    public void setOktmo(String oktmo) {
        this.oktmo = oktmo;
    }

    public String getOktmo() {
        return oktmo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getCapital() {
        return capital;
    }

    public void setOsmId(String osmId) {
        this.osmId = osmId;
    }

    public String getOsmId() {
        return osmId;
    }


}