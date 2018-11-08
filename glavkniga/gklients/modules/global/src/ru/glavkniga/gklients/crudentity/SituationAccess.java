/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crudentity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import java.util.Date;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;
import ru.glavkniga.gklients.entity.Client;

/**
 * @author LysovIA
 */
@MetaClass(name = "gklients$SituationAccess")
public class SituationAccess extends AbstractNotPersistentEntity {
    private static final long serialVersionUID = 458799554659703677L;

    @MetaProperty
    protected Client client;

    @MetaProperty
    protected Date beginDate;

    @MetaProperty
    protected Date endDate;

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }


}