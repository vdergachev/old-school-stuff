/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.enumeration;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author LysovIA
 */
public enum Services implements EnumClass<Integer> {

    ElverSubscription(1),
    SeminarSubscription(2),
    WebsiteRegistration(3),
    NewsSubscription(4);

    private Integer id;

    Services(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static Services fromId(Integer id) {
        for (Services at : Services.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}