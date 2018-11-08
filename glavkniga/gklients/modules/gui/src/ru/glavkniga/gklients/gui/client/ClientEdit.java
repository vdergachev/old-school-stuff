/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.gui.client;

import com.haulmont.cuba.gui.components.AbstractEditor;
import ru.glavkniga.gklients.entity.Client;

import java.util.Map;

/**
 * @author LysovIA
 */
public class ClientEdit extends AbstractEditor<Client> {

    @Override
    public void init(Map<String, Object> params) {
        getDialogOptions().setWidth(450);
    }

}

