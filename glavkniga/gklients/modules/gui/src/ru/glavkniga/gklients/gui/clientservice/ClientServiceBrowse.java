/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.clientservice;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.PopupButton;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientService;
import ru.glavkniga.gklients.entity.ElverSubscription;
import ru.glavkniga.gklients.entity.OnetimeMailing;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * @author LysovIA
 */
public class ClientServiceBrowse extends AbstractLookup {

    @Named("clientServicesDs")
    private CollectionDatasource<ClientService, UUID> clientServicesDs;

    @Named("multiselectBtn")
    private PopupButton multiselectBtn;

    @Named("clientServicesTable")
    private Table<ClientService> clientServicesTable;

    @Inject
    private Metadata metadata;

    @Override
    public void init(Map<String, Object> params) {
        clientServicesDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                multiselectBtn.setEnabled(true);
            } else {
                multiselectBtn.setEnabled(false);
            }
        });
    }

    public void onMakeOntimeSending(Component source) {
        Set<ClientService> clientServices = clientServicesTable.getSelected();
        Set<Client> clients = new HashSet<>();

        if (clientServices != null) {
            clientServices.forEach(clientService -> clients.add(clientService.getClient()));
            Map<String, Object> params = new HashMap<>();
            OnetimeMailing mailing = metadata.create(OnetimeMailing.class);
            params.put("CLIENTS", clients);
            openEditor(mailing, WindowManager.OpenType.DIALOG, params);
        }
    }
    public void onSubscribeToDistribution(Component source) {
        Set<ClientService> services = clientServicesTable.getSelected();
        Set<Client> clients = new HashSet<>();

        if (services != null) {
            services.forEach(service -> clients.add(service.getClient()));
            Map<String, Object> params = new HashMap<>();
            params.put("CLIENTS", clients);
            openWindow("mass-distribution-subscription-add", WindowManager.OpenType.DIALOG.height(300).width(750), params);
        }

    }
}