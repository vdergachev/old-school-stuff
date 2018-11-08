/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import ru.glavkniga.gklients.enumeration.AttachmentMethod;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

/**
 * @author IgorLysov
 */
@NamePattern("%s|url")
@Table(name = "GKLIENTS_ATTACHMENT")
@Entity(name = "gklients$Attachment")
public class Attachment extends StandardEntity {
    private static final long serialVersionUID = 2440279724854728435L;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open"})
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @Lob
    @Column(name = "URL")
    protected String url;

    @NotNull
    @Column(name = "ATTACHMENT_METHOD", nullable = false)
    protected Integer attachmentMethod;

    @JoinTable(name = "GKLIENTS_EMAIL_TEMPLATE_ATTACHMENT_LINK",
        joinColumns = @JoinColumn(name = "ATTACHMENT_ID"),
        inverseJoinColumns = @JoinColumn(name = "EMAIL_TEMPLATE_ID"))
    @ManyToMany
    protected List<EmailTemplate> emailTemplates;

    @JoinTable(name = "GKLIENTS_ONETIME_MAILING_ATTACHMENT_LINK",
        joinColumns = @JoinColumn(name = "ATTACHMENT_ID"),
        inverseJoinColumns = @JoinColumn(name = "ONETIME_MAILING_ID"))
    @ManyToMany
    protected List<OnetimeMailing> onetimeMailings;

    @JoinTable(name = "GKLIENTS_DISTRIBUTION_ATTACHMENT_LINK",
        joinColumns = @JoinColumn(name = "ATTACHMENT_ID"),
        inverseJoinColumns = @JoinColumn(name = "DISTRIBUTION_ID"))
    @ManyToMany
    protected List<Distribution> distributions;

    public void setDistributions(List<Distribution> distributions) {
        this.distributions = distributions;
    }

    public List<Distribution> getDistributions() {
        return distributions;
    }


    public void setOnetimeMailings(List<OnetimeMailing> onetimeMailings) {
        this.onetimeMailings = onetimeMailings;
    }

    public List<OnetimeMailing> getOnetimeMailings() {
        return onetimeMailings;
    }


    public void setEmailTemplates(List<EmailTemplate> emailTemplates) {
        this.emailTemplates = emailTemplates;
    }

    public List<EmailTemplate> getEmailTemplates() {
        return emailTemplates;
    }


    public void setAttachmentMethod(AttachmentMethod attachmentMethod) {
        this.attachmentMethod = attachmentMethod == null ? null : attachmentMethod.getId();
    }

    public AttachmentMethod getAttachmentMethod() {
        return attachmentMethod == null ? null : AttachmentMethod.fromId(attachmentMethod);
    }


    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }


}