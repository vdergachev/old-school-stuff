/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.enumeration;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author IgorLysov
 */
public enum AttachmentMethod implements EnumClass<Integer> {

    Embedded(0),
    Separate(1);

    private Integer id;

    AttachmentMethod(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static AttachmentMethod fromId(Integer id) {
        for (AttachmentMethod at : AttachmentMethod.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}