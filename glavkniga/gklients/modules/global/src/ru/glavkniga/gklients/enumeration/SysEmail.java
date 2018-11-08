/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.enumeration;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * @author LysovIA
 */
public enum SysEmail implements EnumClass<Integer>{

    ONETIME_MAILING(0),
    REGKEY_ACTIVATION(1),
    PASSWORD_CHANGE(2),
    PASSWORD_REMINDER(3),
    RIZ_NOTIFICATION(4),
    NEW_ISSUE(5),
    EMAIL_CHANGE(6),
    NEWS_NEWS(7),
    NEWS_ANNOTATION(8),
    NEWS_STATUS(9),
    NEWS_TAX(12),
    NEWS_INTRO(10),
    NEWS_TEMPLATE(11);

    private Integer id;

    SysEmail (Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    public static SysEmail fromId(Integer id) {
        for (SysEmail at : SysEmail.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}