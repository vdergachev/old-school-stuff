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
 * @author Alex
 */
@NamePattern("%s|originText")
@Table(name = "gklients_ORIGIN")
@Entity(name = "gklients$Origin")
public class Origin extends StandardEntity {
    private static final long serialVersionUID = 2576094127163611453L;

    @Column(name = "ORIGIN_TEXT")
    protected String originText;

    public void setOriginText(String originText) {
        this.originText = originText;
    }

    public String getOriginText() {
        return originText;
    }


}