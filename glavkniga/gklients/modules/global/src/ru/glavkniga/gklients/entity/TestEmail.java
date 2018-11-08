/*
 * Copyright (c) 2016 gklients
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.Set;
import javax.persistence.OneToMany;

/**
 * @author LysovIA
 */
@NamePattern("%s|email")
@Table(name = "GKLIENTS_TEST_EMAIL")
@Entity(name = "gklients$TestEmail")
public class TestEmail extends StandardEntity {
    private static final long serialVersionUID = -5168689350209162086L;

    @Column(name = "EMAIL")
    protected String email;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_SAVE")
    protected Date dateSave;

    @Column(name = "EMAIL_INDEX")
    protected String emailIndex;



    public void setEmailIndex(String emailIndex) {
        this.emailIndex = emailIndex;
    }

    public String getEmailIndex() {
        return emailIndex;
    }



    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setDateSave(Date dateSave) {
        this.dateSave = dateSave;
    }

    public Date getDateSave() {
        return dateSave;
    }


}