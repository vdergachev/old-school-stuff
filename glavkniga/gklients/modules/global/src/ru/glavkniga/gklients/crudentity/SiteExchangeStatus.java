/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.crudentity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author Хомяк
 */
public enum SiteExchangeStatus implements EnumClass<String> {

    NEW("NEW"),
    IN_PROGRESS("IN_PROGRESS"),
    PROCESSED("PROCESSED");

    private String id;

    SiteExchangeStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static SiteExchangeStatus fromId(String id) {
        for (SiteExchangeStatus at : SiteExchangeStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}