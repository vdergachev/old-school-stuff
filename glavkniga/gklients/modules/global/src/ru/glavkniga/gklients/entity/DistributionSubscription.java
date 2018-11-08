/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import ru.glavkniga.gklients.enumeration.SubscriptionStatus;

/**
 * @author Igor Lysov
 */
@NamePattern("%s - %s|client,distribution")
@Table(name = "GKLIENTS_DISTRIBUTION_SUBSCRIPTION")
@Entity(name = "gklients$DistributionSubscription")
public class DistributionSubscription extends StandardEntity {
    private static final long serialVersionUID = 4316017967015471504L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISTRIBUTION_ID")
    protected Distribution distribution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID")
    protected Client client;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_BEGIN")
    protected Date dateBegin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_STATUS_UPDATE")
    protected Date dateStatusUpdate;

    @Column(name = "STATUS")
    protected Integer status;

    @Column(name = "EMAIL")
    protected String email;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }



    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    public Distribution getDistribution() {
        return distribution;
    }

    public void setDateBegin(Date dateBegin) {
        this.dateBegin = dateBegin;
    }

    public Date getDateBegin() {
        return dateBegin;
    }

    public void setDateStatusUpdate(Date dateStatusUpdate) {
        this.dateStatusUpdate = dateStatusUpdate;
    }

    public Date getDateStatusUpdate() {
        return dateStatusUpdate;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public SubscriptionStatus getStatus() {
        return status == null ? null : SubscriptionStatus.fromId(status);
    }


}