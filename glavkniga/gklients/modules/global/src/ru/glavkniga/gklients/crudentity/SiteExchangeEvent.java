/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.crudentity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author Хомяк
 */
public enum SiteExchangeEvent implements EnumClass<String> {

    CREATED("CREATED"),
    UPDATED("UPDATED"),
    DELETED("DELETED");

    private String id;

    SiteExchangeEvent(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static SiteExchangeEvent fromId(String id) {
        for (SiteExchangeEvent at : SiteExchangeEvent.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

    
}