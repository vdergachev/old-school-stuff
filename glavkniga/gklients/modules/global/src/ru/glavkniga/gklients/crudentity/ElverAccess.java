/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crudentity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;
import ru.glavkniga.gklients.entity.Client;

import java.util.Date;

/**
 * @author LysovIA
 */
@MetaClass(name = "gklients$ElverAccess")
public class ElverAccess extends AbstractNotPersistentEntity {
    private static final long serialVersionUID = 950039282017495417L;

    @MetaProperty
    protected Client client;

    @MetaProperty
    protected String number;

    @MetaProperty
    protected String year;

    @MetaProperty
    protected Date beginDate;

    @MetaProperty
    protected Date endDate;

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }


    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }


}