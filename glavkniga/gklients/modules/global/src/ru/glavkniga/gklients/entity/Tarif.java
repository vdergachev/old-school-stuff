/*
 * Copyright (c) 2015 ru.glavkniga.gklients.entity.
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author LysovIA
 */
@NamePattern("%s|tarifName")
@Table(name = "gklients_TARIF")
@Entity(name = "gklients$Tarif")
public class Tarif extends StandardEntity {
    private static final long serialVersionUID = -209781481197689391L;

    @Column(name = "TARIF_NAME", length = 25)
    protected String tarifName;

    @Column(name = "TARIF_PRICE")
    protected Double tarifPrice;

    @Column(name = "TARIF_NUMBER", nullable = false)
    protected Integer tarifNumber;

    public void setTarifNumber(Integer tarifNumber) {
        this.tarifNumber = tarifNumber;
    }

    public Integer getTarifNumber() {
        return tarifNumber;
    }


    public void setTarifName(String tarifName) {
        this.tarifName = tarifName;
    }

    public String getTarifName() {
        return tarifName;
    }

    public void setTarifPrice(Double tarifPrice) {
        this.tarifPrice = tarifPrice;
    }

    public Double getTarifPrice() {
        return tarifPrice;
    }


}