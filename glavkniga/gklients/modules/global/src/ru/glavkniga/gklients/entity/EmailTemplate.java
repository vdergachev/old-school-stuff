/*
 * Copyright (c) 2015 ru.glavkniga.gklients.entity.
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import ru.glavkniga.gklients.enumeration.SysEmail;

import javax.validation.constraints.NotNull;
import java.util.List;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Lob;

/**
 * @author LysovIA
 */
@NamePattern("%s|name")
@Table(name = "gklients_EMAIL_TEMPLATE")
@Entity(name = "gklients$EmailTemplate")
public class EmailTemplate extends StandardEntity {
    private static final long serialVersionUID = -6608732580680455566L;

    @Lob
    @Column(name = "NAME")
    protected String name;

    @NotNull
    @Column(name = "SUBJECT", nullable = false)
    protected String subject;

    @Lob
    @NotNull
    @Column(name = "BODY_", nullable = false)
    protected String body;

    @NotNull
    @Column(name = "EMAIL_TYPE", nullable = false)
    protected Integer emailType;

    @JoinTable(name = "GKLIENTS_EMAIL_TEMPLATE_ATTACHMENT_LINK",
        joinColumns = @JoinColumn(name = "EMAIL_TEMPLATE_ID"),
        inverseJoinColumns = @JoinColumn(name = "ATTACHMENT_ID"))
    @ManyToMany
    protected List<Attachment> attachments;


    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }




    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setEmailType(SysEmail emailType) {
        this.emailType = emailType == null ? null : emailType.getId();
    }

    public SysEmail getEmailType() {
        return emailType == null ? null : SysEmail.fromId(emailType);
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


}