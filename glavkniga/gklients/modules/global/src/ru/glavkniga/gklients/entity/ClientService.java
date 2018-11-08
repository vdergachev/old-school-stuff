/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;

import javax.persistence.*;
import java.util.Date;

/**
 * @author LysovIA
 */
@NamePattern("%s %s|client,service")
@Table(name = "GKLIENTS_CLIENT_SERVICE")
@Entity(name = "gklients$ClientService")
public class ClientService extends StandardEntity {
    private static final long serialVersionUID = -5721806154594093730L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID")
    protected Client client;

    @Column(name = "SERVICE", length = 3)
    protected String service;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ACTIVATION_DATE")
    protected Date activationDate;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }


    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }

    public Date getActivationDate() {
        return activationDate;
    }


}