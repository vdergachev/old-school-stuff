/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import ru.glavkniga.gklients.enumeration.DistributionStatus;

import javax.persistence.OrderBy;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * @author Igor Lysov
 */
@NamePattern("%s|name")
@Table(name = "GKLIENTS_DISTRIBUTION")
@Entity(name = "gklients$Distribution")
public class Distribution extends StandardEntity {
    private static final long serialVersionUID = -5251478227210296988L;

    @Column(name = "NAME")
    protected String name;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_ID")
    protected EmailTemplate template;

    @Column(name = "SUBJECT")
    protected String subject;

    @Lob
    @Column(name = "CONTENT")
    protected String content;

    @Column(name = "CREATE_ALLOWED")
    protected Boolean createAllowed;

    @Column(name = "SIGNED_BY_DEFAULT")
    protected Boolean signedByDefault;

    @Column(name = "PERSONAL")
    protected Boolean personal;

    @Column(name = "IMPORTANT")
    protected Boolean important;

    @Column(name = "STATUS")
    protected Integer status;


    @OrderBy("client.email ASC")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "distribution")
    protected List<DistributionSubscription> distributionSubscription;

    @JoinTable(name = "GKLIENTS_DISTRIBUTION_ATTACHMENT_LINK",
        joinColumns = @JoinColumn(name = "DISTRIBUTION_ID"),
        inverseJoinColumns = @JoinColumn(name = "ATTACHMENT_ID"))
    @ManyToMany
    protected List<Attachment> attachments;


    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }




    public void setDistributionSubscription(List<DistributionSubscription> distributionSubscription) {
        this.distributionSubscription = distributionSubscription;
    }

    public List<DistributionSubscription> getDistributionSubscription() {
        return distributionSubscription;
    }


    public void setStatus(DistributionStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public DistributionStatus getStatus() {
        return status == null ? null : DistributionStatus.fromId(status);
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setCreateAllowed(Boolean createAllowed) {
        this.createAllowed = createAllowed;
    }

    public Boolean getCreateAllowed() {
        return createAllowed;
    }

    public void setSignedByDefault(Boolean signedByDefault) {
        this.signedByDefault = signedByDefault;
    }

    public Boolean getSignedByDefault() {
        return signedByDefault;
    }

    public void setPersonal(Boolean personal) {
        this.personal = personal;
    }

    public Boolean getPersonal() {
        return personal;
    }

    public void setImportant(Boolean important) {
        this.important = important;
    }

    public Boolean getImportant() {
        return important;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTemplate(EmailTemplate template) {
        this.template = template;
    }

    public EmailTemplate getTemplate() {
        return template;
    }


}