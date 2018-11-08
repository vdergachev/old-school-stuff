/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.enumeration;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author Igor Lysov
 */
public enum OnetimeMailingStatus implements EnumClass<Integer> {

    New(0),
    Planned(1),
    Sending(2),
    Queued(3),
    Completed(4),
    Error(5);

    private Integer id;

    OnetimeMailingStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static OnetimeMailingStatus fromId(Integer id) {
        for (OnetimeMailingStatus at : OnetimeMailingStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}