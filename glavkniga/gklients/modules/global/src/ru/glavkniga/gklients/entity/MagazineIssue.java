/*
 * Copyright (c) 2015 ru.glavkniga.gklients.entity.
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
import com.haulmont.cuba.core.entity.annotation.Listeners;

/**
 * @author LysovIA
 */
@NamePattern("%s|code")
@Table(name = "gklients_MAGAZINE_ISSUE")
@Entity(name = "gklients$MagazineIssue")
public class MagazineIssue extends StandardEntity {
    private static final long serialVersionUID = -8720405976871692291L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MAGAZINE_ID")
    protected Magazine magazine;

    @Column(name = "MONTH_", length = 2)
    protected String month;

    @Column(name = "YEAR_", length = 4)
    protected String year;

    @Column(name = "NUMBER_", length = 10)
    protected String number;
    @Column(name = "CODE", length = 20)
    protected String code;

    @Temporal(TemporalType.DATE)
    @Column(name = "ISSUE_PLANNED_DATE")
    protected Date issuePlannedDate;

    @Column(name = "PRICE")
    protected Double price;

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }


    public void setIssuePlannedDate(Date issuePlannedDate) {
        this.issuePlannedDate = issuePlannedDate;
    }

    public Date getIssuePlannedDate() {
        return issuePlannedDate;
    }



    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setMagazine(Magazine magazine) {
        this.magazine = magazine;
    }

    public Magazine getMagazine() {
        return magazine;
    }

}