/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.crudentity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author Хомяк
 */
@NamePattern("%s %s %s|day,mon,year")
@MetaClass(name = "gklients$DateException")
public class DateException extends AbstractNotPersistentEntity {
    private static final long serialVersionUID = 3421202875235176515L;

    @MetaProperty
    protected Integer siteId;

    @MetaProperty
    protected Integer day;

    @MetaProperty
    protected Integer mon;

    @MetaProperty
    protected Integer year;

    @MetaProperty
    protected Integer status;

    @MetaProperty
    protected Integer region;

    @MetaProperty
    protected String note;

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getDay() {
        return day;
    }

    public void setMon(Integer mon) {
        this.mon = mon;
    }

    public Integer getMon() {
        return mon;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getYear() {
        return year;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setRegion(Integer region) {
        this.region = region;
    }

    public Integer getRegion() {
        return region;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }


}