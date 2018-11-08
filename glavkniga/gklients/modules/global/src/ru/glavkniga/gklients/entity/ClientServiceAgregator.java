/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;

import java.util.Date;
import java.util.UUID;

/**
 * @author LysovIA
 */
@MetaClass(name = "gklients$ClientServiceAgregator")
public class ClientServiceAgregator extends AbstractNotPersistentEntity {
    private static final long serialVersionUID = 7752176502362823983L;

    @MetaProperty
    protected UUID client;

    @MetaProperty
    protected String email;

    @MetaProperty
    protected Date service1;

    @MetaProperty
    protected Date service2;

    @MetaProperty
    protected Date service3;

    @MetaProperty
    protected Date service4;

    @MetaProperty
    protected Date service5;

    public Date getService1() {
        return service1;
    }

    public void setService1(Date service1) {
        this.service1 = service1;
    }


    public Date getService2() {
        return service2;
    }

    public void setService2(Date service2) {
        this.service2 = service2;
    }


    public Date getService3() {
        return service3;
    }

    public void setService3(Date service3) {
        this.service3 = service3;
    }


    public Date getService4() {
        return service4;
    }

    public void setService4(Date service4) {
        this.service4 = service4;
    }


    public Date getService5() {
        return service5;
    }

    public void setService5(Date service5) {
        this.service5 = service5;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }


    public UUID getClient() {
        return client;
    }

    public void setClient(UUID client) {
        this.client = client;
    }


}