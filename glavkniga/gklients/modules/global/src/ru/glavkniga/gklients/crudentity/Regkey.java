/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.crudentity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import java.util.Date;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;

/**
 * @author LysovIA
 */
@MetaClass(name = "gklients$Regkey")
public class Regkey extends AbstractNotPersistentEntity {
    private static final long serialVersionUID = 3581653699153580372L;

    @MetaProperty
    protected String email;

    @MetaProperty
    protected String regkey;

    @MetaProperty
    protected Date date_activation;

    @MetaProperty
    protected String tarif;

    public void setTarif(String tarif) {
        this.tarif = tarif;
    }

    public String getTarif() {
        return tarif;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setRegkey(String regkey) {
        this.regkey = regkey;
    }

    public String getRegkey() {
        return regkey;
    }

    public void setDate_activation(Date date_activation) {
        this.date_activation = date_activation;
    }

    public Date getDate_activation() {
        return date_activation;
    }


}