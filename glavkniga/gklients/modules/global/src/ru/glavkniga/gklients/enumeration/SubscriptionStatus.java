/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.enumeration;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author Igor Lysov
 */
public enum SubscriptionStatus implements EnumClass<Integer> {
    notSubscribed(0),
    subscribed(1),
    unSubscribed(2);

    private Integer id;

    SubscriptionStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static SubscriptionStatus fromId(Integer id) {
        for (SubscriptionStatus at : SubscriptionStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}