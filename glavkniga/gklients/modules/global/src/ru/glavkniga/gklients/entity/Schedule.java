/*
 * Copyright (c) 2015 ru.glavkniga.gklients.entity.
 */
package ru.glavkniga.gklients.entity;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.Date;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author LysovIA
 */
@NamePattern("%s|comments")
@Table(name = "gklients_SCHEDULE")
@Entity(name = "gklients$Schedule")
public class Schedule extends StandardEntity {
    private static final long serialVersionUID = 794594221435828619L;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MAGAZINE_ISSUE_ID")
    protected MagazineIssue magazineIssue;

    @Column(name = "COMMENTS")
    protected String comments;

    @Temporal(TemporalType.DATE)
    @Column(name = "ISSUE_START")
    protected Date issueStart;

    @Temporal(TemporalType.DATE)
    @Column(name = "ISSUE_FINISH")
    protected Date issueFinish;

    @Temporal(TemporalType.DATE)
    @Column(name = "ISSUE_SIGN")
    protected Date issueSign;

    @Temporal(TemporalType.DATE)
    @Column(name = "PRINTING_RECEVIE")
    protected Date printingRecevie;

    @Temporal(TemporalType.DATE)
    @Column(name = "SITE_UPLOAD")
    protected Date siteUpload;

    @Temporal(TemporalType.DATE)
    @Column(name = "DELIVERY_DATE")
    protected Date deliveryDate;

    public void setMagazineIssue(MagazineIssue magazineIssue) {
        this.magazineIssue = magazineIssue;
    }

    public MagazineIssue getMagazineIssue() {
        return magazineIssue;
    }


    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }

    public void setIssueStart(Date issueStart) {
        this.issueStart = issueStart;
    }

    public Date getIssueStart() {
        return issueStart;
    }

    public void setIssueFinish(Date issueFinish) {
        this.issueFinish = issueFinish;
    }

    public Date getIssueFinish() {
        return issueFinish;
    }

    public void setIssueSign(Date issueSign) {
        this.issueSign = issueSign;
    }

    public Date getIssueSign() {
        return issueSign;
    }

    public void setPrintingRecevie(Date printingRecevie) {
        this.printingRecevie = printingRecevie;
    }

    public Date getPrintingRecevie() {
        return printingRecevie;
    }

    public void setSiteUpload(Date siteUpload) {
        this.siteUpload = siteUpload;
    }

    public Date getSiteUpload() {
        return siteUpload;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }


}