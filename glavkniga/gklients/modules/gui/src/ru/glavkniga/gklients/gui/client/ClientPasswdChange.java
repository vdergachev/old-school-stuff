/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.gui.client;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.service.EmailerService;
import ru.glavkniga.gklients.service.GeneratorService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * @author LysovIA
 */
public class ClientPasswdChange extends AbstractEditor {

    @Named("clientDs")
    Datasource<Client> clientDs;

    @Named("passEnter")
    TextField passEnter;

    @Named("passRetype")
    TextField passRetype;

    @Inject
    protected GeneratorService generatorService;

    @Inject
    protected EmailerService emailerService;


    @Override
    public void init(Map<String, Object> params) {
        getDialogOptions().setWidth(500);

    }

    @Override
    public boolean preCommit() {

        if (passEnter.getValue().equals(passRetype.getValue())) {
            Client client = clientDs.getItem();
            client.setPassword(passEnter.getValue());
            showOptionDialog(
                    getMessage("msConfirm"),
                    getMessage("msMsg"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    Client client = clientDs.getItem();
                                    emailerService.changePassword(client);
                                }
                            },
                            new DialogAction(DialogAction.Type.NO) {

                            }
                    }
            );
            return true;
        } else {
            String title = getMessage("passMismatchTitle");
            String msg = getMessage("passMismatchMsg");
            showMessageDialog(title, msg, MessageType.WARNING);
            return false;
        }
    }


    public void onGenerateBtnClick(Component source) {
        String pass = generatorService.generatePass(clientDs.getItem().getEmail());
        passEnter.setValue(pass);
        passRetype.setValue(pass);
    }
}