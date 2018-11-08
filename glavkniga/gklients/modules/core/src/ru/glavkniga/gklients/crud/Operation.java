/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crud;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * Created by Igor Lysov on 05.03.2017.
 */


/*
* $signs = array('=', '<', '<=', '>', '>=', '!=', '<>', 'NOT IN');
&filter[sdfsd][NOT IN][1]=234
&filter[sdfsd][NOT IN][2]=222
&filter[sdfsd][NOT IN][3]=444
&filter[sdfsd][NOT IN][0]=999
* */
public enum Operation implements EnumClass<String> {

    IS("IS"),
    IS_NOT("IS NOT"),
    LESS("<"),
    MORE(">"),
    LESS_EQUAL("<="),
    MORE_EQUAL(">="),
    EQUAL("="),
    NOT_EQUAL("!="),
    LESS_MORE("<>"),
    MORE_LESS("<>"),
    IN("IN"),
    NOT_IN("NOT IN");

    private String id;

    Operation(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    public static Operation fromId(String id) {
        for (Operation at : Operation.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
