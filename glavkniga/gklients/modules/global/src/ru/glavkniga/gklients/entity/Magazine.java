/*
 * Copyright (c) 2015 ru.glavkniga.gklients.entity.
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;

/**
 * @author LysovIA
 */
@NamePattern("%s|magazineAbb")
@Table(name = "gklients_MAGAZINE")
@Entity(name = "gklients$Magazine")
public class Magazine extends StandardEntity {
    private static final long serialVersionUID = -7792205589268730409L;

    @Column(name = "MAGAZINE_NAME", length = 50)
    protected String magazineName;

    @Column(name = "MAGAZINE_ABB", length = 5)
    protected String magazineAbb;

    @Column(name = "MAGAZINE_I_D", nullable = false)
    protected Integer magazineID;

    public void setMagazineID(Integer magazineID) {
        this.magazineID = magazineID;
    }

    public Integer getMagazineID() {
        return magazineID;
    }


    public void setMagazineAbb(String magazineAbb) {
        this.magazineAbb = magazineAbb;
    }

    public String getMagazineAbb() {
        return magazineAbb;
    }


    public void setMagazineName(String magazineName) {
        this.magazineName = magazineName;
    }

    public String getMagazineName() {
        return magazineName;
    }


}