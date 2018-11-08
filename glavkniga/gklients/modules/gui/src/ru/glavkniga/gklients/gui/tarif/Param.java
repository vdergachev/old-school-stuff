/*
 * Copyright (c) 2015 gklients
 */

package ru.glavkniga.gklients.gui.tarif;

/**
 * Created by LysovIA on 02.12.2015.
 */
public class Param {
    private String name;
    private String value;

    public Param(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
