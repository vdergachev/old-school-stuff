/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.gui.client;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ElverSubscription;
import ru.glavkniga.gklients.entity.OnetimeMailing;
import ru.glavkniga.gklients.service.EmailerService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author LysovIA
 */
public class ClientBrowse extends AbstractLookup {

    @Named("clientsTable")
    Table<Client> clientsTable;

    @Inject
    private EmailerService emailerService;

    @Named("changePassBtn")
    Button changePassBtn;

    @Inject
    private Metadata metadata;

    @Named("clientsDs")
    private CollectionDatasource<Client,UUID> clientsDs;

    @Named("notificationBtn")
    private PopupButton notificationBtn;

    @Named("multiselectBtn")
    private PopupButton multiselectBtn;


    @Named("clientsTable.edit")
    private EditAction clientsTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        clientsTableEdit.getBulkEditorIntegration()
                .setEnabled(true)
                .setOpenType(WindowManager.OpenType.DIALOG)
                .setExcludePropertiesRegex("name|email|password|emailHash|passwordHash|itn|clientDistributionSettings|phone");


        EditAction passChangeAction = new EditAction(clientsTable, WindowManager.OpenType.DIALOG, "passChange");
        passChangeAction.setWindowId("gklients$Client.password.edit");
        clientsTable.addAction(passChangeAction);
        changePassBtn.setAction(passChangeAction);

        clientsDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                multiselectBtn.setEnabled(true);
                if (clientsTable.getSelected().size() == 1){
                    notificationBtn.setEnabled(true);
                } else
                {
                    notificationBtn.setEnabled(false);
                }
            } else {
                multiselectBtn.setEnabled(false);
                notificationBtn.setEnabled(false);
            }
        });
    }

    public void passRemindPerform(Component source) {
        Client client = clientsTable.getSingleSelected();
        if (null != client) {
            showOptionDialog(
                    getMessage("msConfirm"),
                    getMessage("msRemind"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    emailerService.remindPassword(client);
                                }
                            },
                            new DialogAction(DialogAction.Type.NO) {
                            }
                    }
            );
        } else {
            showMessageDialog(getMessage("passMismatchTitle"), getMessage("msNoRowSelectedV"), MessageType.WARNING);
        }
    }

    public void emailChangePerform(Component source) {
        Client client = clientsTable.getSingleSelected();
        if (null != client) {
            showOptionDialog(
                    getMessage("msConfirm"),
                    getMessage("msEmailChange"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    emailerService.changeEmail(client);
                                }
                            },
                            new DialogAction(DialogAction.Type.NO) {
                            }
                    }
            );
        } else {
            showMessageDialog(getMessage("passMismatchTitle"), getMessage("msNoRowSelectedV"), MessageType.WARNING);
        }
    }

    public void onMakeOntimeSending(Component source) {
        Set<Client> clients = clientsTable.getSelected();

        if (clients != null) {
            Map<String, Object> params = new HashMap<>();
            OnetimeMailing mailing = metadata.create(OnetimeMailing.class);
            params.put("CLIENTS", clients);
            openEditor(mailing, WindowManager.OpenType.DIALOG, params);
        }
    }

    public void onSubscribeToDistribution(Component source) {
        Set<Client> clients = clientsTable.getSelected();

        if (clients != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("CLIENTS", clients);
            openWindow("mass-distribution-subscription-add", WindowManager.OpenType.DIALOG.height(300).width(750), params);
        }
    }

}