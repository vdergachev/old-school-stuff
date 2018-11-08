/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.distributionSubscriptionComposition;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientDistributionSettings;
import ru.glavkniga.gklients.entity.Distribution;
import ru.glavkniga.gklients.entity.DistributionSubscription;
import ru.glavkniga.gklients.enumeration.SubscriptionStatus;
import ru.glavkniga.gklients.service.DateTimeService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * @author IgorLysov
 */
public class MassDistributionSubscriptionAdd extends AbstractWindow {

    @Named("externalClientsDs")
    CollectionDatasource<Client, UUID> externalClientsDs;

    @Named("distributionsDs")
    private
    CollectionDatasource<Distribution, UUID> distributionsDs;

    @Named("newClientDistributionSettingsDs")
    private CollectionDatasource<ClientDistributionSettings, UUID> newClientDistributionSettingsDs;

    @Named("newDistributionSubscriptionsDs")
    private CollectionDatasource<DistributionSubscription, UUID> newDistributionSubscriptionsDs;

    @Inject
    private Metadata metadata;

    @Named("clientDistributionSettingsDs")
    private Datasource<ClientDistributionSettings> clientDistributionSettingsDs;

    @Override
    public void init(Map<String, Object> params) {


        if (params.containsKey("CLIENTS")) {
            Collection clients = (Collection) params.get("CLIENTS");
            externalClientsDs.refresh(ParamsMap.of("clients", clients));
        }

//        if (params.containsKey("CLIENTS")) {
//            try {
//                Set<Client> clients = (Set<Client>) params.get("CLIENTS"); //TODO Fix cast here
//
//            } catch (ClassCastException e) {
//                e.printStackTrace();
//            }
            super.init(params);
//        }
    }

    @Inject
    private MetadataTools metadataTools;

    @Inject
    private DateTimeService dts;

    @Override
    public void ready() {
        ClientDistributionSettings item = metadata.create(ClientDistributionSettings.class);
        item.setIsCompany(Boolean.TRUE);
        item.setIsCivil(Boolean.TRUE);
        item.setIsIp(Boolean.TRUE);
        item.setIsWithWorkers(Boolean.TRUE);
        item.setIsWithoutWorkers(Boolean.TRUE);
        item.setIsOsno(Boolean.TRUE);
        item.setIsUsn(Boolean.TRUE);
        item.setIsEnvd(Boolean.TRUE);
        item.setIsEshn(Boolean.TRUE);
        item.setIsPsn(Boolean.TRUE);
        clientDistributionSettingsDs.setItem(item);
    }

    public void windowClose() {
        getWindowManager().close(this);
    }

    public void saveAndClose() {
        Collection<Client> clients = externalClientsDs.getItems();
        for (Client client : clients) {
            ClientDistributionSettings existingSettings = client.getClientDistributionSettings();

            if (existingSettings == null) {
                ClientDistributionSettings clientDistributionSettings = metadataTools.copy(clientDistributionSettingsDs.getItem());
                clientDistributionSettings.setId(UUID.randomUUID());
                clientDistributionSettings.setClient(client);
                newClientDistributionSettingsDs.addItem(clientDistributionSettings);
            }

            List<DistributionSubscription> existingDS = client.getDistributionSubscription();
            Distribution distToSubscribe = distributionsDs.getItem();
            Boolean found = false;
            for (DistributionSubscription ds : existingDS) {
                UUID existingDistId = ds.getDistribution().getId();
                if (distToSubscribe.getId().equals(existingDistId)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                DistributionSubscription distributionSubscription = metadata.create(DistributionSubscription.class);
                distributionSubscription.setClient(client);
                distributionSubscription.setStatus(SubscriptionStatus.subscribed);
                distributionSubscription.setDateStatusUpdate(dts.getCurrentDateTime());
                distributionSubscription.setDateBegin(dts.getCurrentDateTime());
                distributionSubscription.setDistribution(distToSubscribe);
                newDistributionSubscriptionsDs.addItem(distributionSubscription);
            }
        }

        if (newClientDistributionSettingsDs.size() > 0)
            newClientDistributionSettingsDs.commit();

        if (newDistributionSubscriptionsDs.size() > 0)
            newDistributionSubscriptionsDs.commit();
        getWindowManager().close(this);
    }
}