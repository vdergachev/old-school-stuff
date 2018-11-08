/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author Хомяк
 */
@NamePattern("%s|client")
@Table(name = "GKLIENTS_FAILED_EMAILS")
@Entity(name = "gklients$FailedEmail")
public class FailedEmail extends StandardEntity {
    private static final long serialVersionUID = 1387443231328635433L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID")
    protected Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ONETIME_MAILING_ID")
    protected OnetimeMailing onetimeMailing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MAILING_STATISTICS_ID")
    protected MailingStatistics mailingStatistics;

    @Column(name = "REASON")
    protected String reason;

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setOnetimeMailing(OnetimeMailing onetimeMailing) {
        this.onetimeMailing = onetimeMailing;
    }

    public OnetimeMailing getOnetimeMailing() {
        return onetimeMailing;
    }

    public void setMailingStatistics(MailingStatistics mailingStatistics) {
        this.mailingStatistics = mailingStatistics;
    }

    public MailingStatistics getMailingStatistics() {
        return mailingStatistics;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }


}