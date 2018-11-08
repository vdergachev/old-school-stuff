/*
 * Copyright (c) 2015 ru.glavkniga.gklients.entity.
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.entity.annotation.Listeners;

/**
 * @author LysovIA
 */
@Listeners("gklients_ElverSubscriptionListener")
@NamePattern("%s|client")
@Table(name = "GKLIENTS_ELVER_SUBSCRIPTION")
@Entity(name = "gklients$ElverSubscription")
public class ElverSubscription extends StandardEntity {
    private static final long serialVersionUID = 1047595605038538771L;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID")
    protected Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RIZ_ID")
    protected Riz riz;

    @Column(name = "REGKEY", unique = true, length = 16)
    protected String regkey;



    @Temporal(TemporalType.DATE)
    @Column(name = "REQUEST_DATE")
    protected Date requestDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TARIF_ID")
    protected Tarif tarif;

    @OnDeleteInverse(DeletePolicy.DENY)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ISSUE_START_ID")
    protected MagazineIssue issueStart;


    @OnDeleteInverse(DeletePolicy.DENY)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ISSUE_END_ID")
    protected MagazineIssue issueEnd;

    @Column(name = "IS_REG_KEY_SENT_TO_RIZ")
    protected Boolean isRegKeySentToRiz;

    @Column(name = "IS_REG_KEY_USED")
    protected Boolean isRegKeyUsed;

    @Column(name = "IS_PASS_SENT_TO_CUSTOMER")
    protected Boolean isPassSentToCustomer;

    @Column(name = "IS_TARIF_CHECKED")
    protected Boolean isTarifChecked;

    @Temporal(TemporalType.DATE)
    @Column(name = "ACTIVATION_DATE")
    protected Date activation_date;

    public void setActivation_date(Date activation_date) {
        this.activation_date = activation_date;
    }

    public Date getActivation_date() {
        return activation_date;
    }



    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }


    public void setIsRegKeySentToRiz(Boolean isRegKeySentToRiz) {
        this.isRegKeySentToRiz = isRegKeySentToRiz;
    }

    public Boolean getIsRegKeySentToRiz() {
        return isRegKeySentToRiz;
    }

    public void setIsRegKeyUsed(Boolean isRegKeyUsed) {
        this.isRegKeyUsed = isRegKeyUsed;
    }

    public Boolean getIsRegKeyUsed() {
        return isRegKeyUsed;
    }

    public void setIsPassSentToCustomer(Boolean isPassSentToCustomer) {
        this.isPassSentToCustomer = isPassSentToCustomer;
    }

    public Boolean getIsPassSentToCustomer() {
        return isPassSentToCustomer;
    }

    public void setIsTarifChecked(Boolean isTarifChecked) {
        this.isTarifChecked = isTarifChecked;
    }

    public Boolean getIsTarifChecked() {
        return isTarifChecked;
    }


    public void setIssueEnd(MagazineIssue issueEnd) {
        this.issueEnd = issueEnd;
    }

    public MagazineIssue getIssueEnd() {
        return issueEnd;
    }


    public void setIssueStart(MagazineIssue issueStart) {
        this.issueStart = issueStart;
    }

    public MagazineIssue getIssueStart() {
        return issueStart;
    }





    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setTarif(Tarif tarif) {
        this.tarif = tarif;
    }

    public Tarif getTarif() {
        return tarif;
    }


    public void setRiz(Riz riz) {
        this.riz = riz;
    }

    public Riz getRiz() {
        return riz;
    }




    public void setRegkey(String regkey) { this.regkey = regkey; }

    public String getRegkey() {
        return regkey;
    }






}