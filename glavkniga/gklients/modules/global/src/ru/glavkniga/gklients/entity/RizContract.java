/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author LysovIA
 */
@NamePattern("%s|contractNumber")
@Table(name = "GKLIENTS_RIZ_CONTRACT")
@Entity(name = "gklients$RizContract")
public class RizContract extends StandardEntity {
    private static final long serialVersionUID = 8148000118629624325L;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RIZ_ID")
    protected Riz riz;

    @Column(name = "CONTRACT_NUMBER")
    protected String contractNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "CONTRACT_DATE")
    protected Date contractDate;

    public void setRiz(Riz riz) {
        this.riz = riz;
    }

    public Riz getRiz() {
        return riz;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    public Date getContractDate() {
        return contractDate;
    }


}