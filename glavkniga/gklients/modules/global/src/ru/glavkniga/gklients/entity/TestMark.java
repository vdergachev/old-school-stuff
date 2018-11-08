/*
 * Copyright (c) 2016 gklients
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
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

/**
 * @author LysovIA
 */
@Table(name = "GKLIENTS_TEST_MARK")
@Entity(name = "gklients$TestMark")
public class TestMark extends StandardEntity {
    private static final long serialVersionUID = 2456967957993357106L;

    @Column(name = "MARK")
    protected Double mark;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_PASS")
    protected Date datePass;

    @Column(name = "MARK_INDEX")
    protected Integer markIndex;

    @Column(name = "TEST_INDEX")
    protected Integer testIndex;

    @Column(name = "EMAIL_INDEX")
    protected String emailIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEST_ID")
    protected Test test;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMAIL_ID")
    protected TestEmail email;

    public void setEmail(TestEmail email) {
        this.email = email;
    }

    public TestEmail getEmail() {
        return email;
    }


    public void setEmailIndex(String emailIndex) {
        this.emailIndex = emailIndex;
    }

    public String getEmailIndex() {
        return emailIndex;
    }


    public void setMarkIndex(Integer markIndex) {
        this.markIndex = markIndex;
    }

    public Integer getMarkIndex() {
        return markIndex;
    }



    public void setTest(Test test) {
        this.test = test;
    }

    public Test getTest() {
        return test;
    }


    public void setTestIndex(Integer testIndex) {
        this.testIndex = testIndex;
    }

    public Integer getTestIndex() {
        return testIndex;
    }





    public void setMark(Double mark) {
        this.mark = mark;
    }

    public Double getMark() {
        return mark;
    }

    public void setDatePass(Date datePass) {
        this.datePass = datePass;
    }

    public Date getDatePass() {
        return datePass;
    }


}