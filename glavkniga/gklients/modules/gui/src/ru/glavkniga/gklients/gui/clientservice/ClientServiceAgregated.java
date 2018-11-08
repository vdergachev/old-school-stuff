/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.clientservice;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.PopupButton;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientServiceAgregator;
import ru.glavkniga.gklients.entity.ElverSubscription;
import ru.glavkniga.gklients.entity.OnetimeMailing;
import ru.glavkniga.gklients.service.ServiceAggregatorService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * @author LysovIA
 */
public class ClientServiceAgregated extends AbstractLookup {

    @Named("clientServiceAgregatorsDs")
    CollectionDatasource<ClientServiceAgregator, UUID> clientServiceAgregatorsDs;

    @Named("clientServiceTable")
    Table<ClientServiceAgregator> clientServiceTable;

    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;

    @Named("multiselectBtn")
    private PopupButton multiselectBtn;

    @Override
    public void init(Map<String, Object> params) {
        clientServiceAgregatorsDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                multiselectBtn.setEnabled(true);
            } else {
                multiselectBtn.setEnabled(false);
            }
        });
    }

    public void onMakeOntimeSending(Component source) {
        Set<ClientServiceAgregator> agregators = clientServiceTable.getSelected();
        List<UUID> clientsIDSet = new ArrayList<>();
        if (agregators != null) {
            agregators.forEach(agregator -> clientsIDSet.add(agregator.getClient()));
            List<Client> clients = loadClients(clientsIDSet);
            Map<String, Object> params = new HashMap<>();
            OnetimeMailing mailing = metadata.create(OnetimeMailing.class);
            params.put("CLIENTS", clients);
            openEditor(mailing, WindowManager.OpenType.DIALOG, params);
        }
    }

    public void onSubscribeToDistribution(Component source) {
        Set<ClientServiceAgregator> agregators = clientServiceTable.getSelected();
        List<UUID> clientsIDSet = new ArrayList<>();
        if (agregators != null) {
            agregators.forEach(agregator -> clientsIDSet.add(agregator.getClient()));
            List<Client> clients = loadClients(clientsIDSet);
            Map<String, Object> params = new HashMap<>();
            params.put("CLIENTS", clients);
            openWindow("mass-distribution-subscription-add", WindowManager.OpenType.DIALOG.height(300).width(750), params);
        }

    }

    private List<Client> loadClients(List<UUID> clientsIDSet) {
        LoadContext.Query query = LoadContext.createQuery("select c from gklients$Client c where c.id in :list")
                .setParameter("list", clientsIDSet);
        LoadContext<Client> loadContext = LoadContext.create(Client.class)
                .setQuery(query)
                .setView("_local");
        return dataManager.loadList(loadContext);
    }
}