/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import ru.glavkniga.gklients.enumeration.DistributionStatus;

import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import ru.glavkniga.gklients.enumeration.OnetimeMailingStatus;
import java.util.Set;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import com.haulmont.chile.core.annotations.Composition;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * @author Igor Lysov
 */
@NamePattern("%s|subject")
@Table(name = "GKLIENTS_ONETIME_MAILING")
@Entity(name = "gklients$OnetimeMailing")
public class OnetimeMailing extends StandardEntity {
    private static final long serialVersionUID = -6510412350171499934L;

    @JoinTable(name = "GKLIENTS_ONETIME_MAILING_CLIENT_LINK",
        joinColumns = @JoinColumn(name = "ONETIME_MAILING_ID"),
        inverseJoinColumns = @JoinColumn(name = "CLIENT_ID"))
    @ManyToMany
    protected Set<Client> client;

    @Column(name = "SUBJECT")
    protected String subject;

    @Lob
    @Column(name = "BODY_")
    protected String body;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SENDING_DATE")
    protected Date sendingDate;

    @Column(name = "STATUS")
    protected Integer status;

    @Column(name = "IMPORTANT")
    protected Boolean important;

    @Column(name = "PERSONAL")
    protected Boolean personal;

    @JoinTable(name = "GKLIENTS_ONETIME_MAILING_ATTACHMENT_LINK",
        joinColumns = @JoinColumn(name = "ONETIME_MAILING_ID"),
        inverseJoinColumns = @JoinColumn(name = "ATTACHMENT_ID"))
    @ManyToMany
    protected Set<Attachment> attachments;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_ID")
    protected EmailTemplate template;


    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "onetimeMailing")
    protected MailingStatistics mailingStatistics;

    public void setMailingStatistics(MailingStatistics mailingStatistics) {
        this.mailingStatistics = mailingStatistics;
    }

    public MailingStatistics getMailingStatistics() {
        return mailingStatistics;
    }


    public void setTemplate(EmailTemplate template) {
        this.template = template;
    }

    public EmailTemplate getTemplate() {
        return template;
    }


    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Set<Attachment> getAttachments() {
        return attachments;
    }




    public Set<Client> getClient() {
        return client;
    }

    public void setClient(Set<Client> client) {
        this.client = client;
    }


    public OnetimeMailingStatus getStatus() {
        return status == null ? null : OnetimeMailingStatus.fromId(status);
    }

    public void setStatus(OnetimeMailingStatus status) {
        this.status = status == null ? null : status.getId();
    }



    public void setPersonal(Boolean personal) {
        this.personal = personal;
    }

    public Boolean getPersonal() {
        return personal;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setSendingDate(Date sendingDate) {
        this.sendingDate = sendingDate;
    }

    public Date getSendingDate() {
        return sendingDate;
    }

    public void setImportant(Boolean important) {
        this.important = important;
    }

    public Boolean getImportant() {
        return important;
    }


}