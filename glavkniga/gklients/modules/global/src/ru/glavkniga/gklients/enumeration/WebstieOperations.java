/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.enumeration;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author LysovIA
 */
public enum WebstieOperations implements EnumClass<Integer> {

    Get(1),
    Add(2),
    Edit(3),
    Delete(4);

    private Integer id;

    WebstieOperations(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static WebstieOperations fromId(Integer id) {
        for (WebstieOperations at : WebstieOperations.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}