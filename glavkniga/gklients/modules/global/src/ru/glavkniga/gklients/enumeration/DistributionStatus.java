/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.enumeration;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author Igor Lysov
 */
public enum DistributionStatus implements EnumClass<Integer> {

    added(1),
    sending(2),
    stopped(3);

    private Integer id;

    DistributionStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static DistributionStatus fromId(Integer id) {
        for (DistributionStatus at : DistributionStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}