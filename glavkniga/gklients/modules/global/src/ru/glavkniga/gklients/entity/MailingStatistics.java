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
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * @author Хомяк
 */
@NamePattern("%s|onetimeMailing")
@Table(name = "GKLIENTS_MAILING_STATISTICS")
@Entity(name = "gklients$MailingStatistics")
public class MailingStatistics extends StandardEntity {
    private static final long serialVersionUID = 6975751974277693948L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ONETIME_MAILING_ID")
    protected OnetimeMailing onetimeMailing;

    @Column(name = "PLANNED")
    protected Integer planned;

    @Column(name = "SENDING")
    protected Integer sending;

    @Column(name = "COMPLETED")
    protected Integer completed;

    @Column(name = "FAILED")
    protected Integer failed;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "mailingStatistics")
    protected List<FailedEmail> failedEmails;

    public void setFailedEmails(List<FailedEmail> failedEmails) {
        this.failedEmails = failedEmails;
    }

    public List<FailedEmail> getFailedEmails() {
        return failedEmails;
    }


    public void setOnetimeMailing(OnetimeMailing onetimeMailing) {
        this.onetimeMailing = onetimeMailing;
    }

    public OnetimeMailing getOnetimeMailing() {
        return onetimeMailing;
    }

    public void setPlanned(Integer planned) {
        this.planned = planned;
    }

    public Integer getPlanned() {
        return planned;
    }

    public void setSending(Integer sending) {
        this.sending = sending;
    }

    public Integer getSending() {
        return sending;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public Integer getFailed() {
        return failed;
    }


}