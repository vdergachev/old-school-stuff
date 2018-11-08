/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.distribution;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.Distribution;
import ru.glavkniga.gklients.entity.DistributionSubscription;
import ru.glavkniga.gklients.enumeration.SubscriptionStatus;
import ru.glavkniga.gklients.service.MassMailingService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author Igor Lysov
 */
public class DistributionEdit extends AbstractEditor<Distribution> {

    @Inject
    private MassMailingService massMailingService;

    @Named("distributionDs")
    private Datasource<Distribution> distributionDs;

    @Named("selectedClientsDs")
    private CollectionDatasource<Client, UUID> selectedClientsDs;

    @Named("distributionSubscriptionsDs")
    private CollectionDatasource<DistributionSubscription, UUID> distributionSubscriptionsDs;

    @Named("clientsTokensGB")
    private GroupBoxLayout clientsTokensGB;

    @Inject
    private Metadata metadata;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        //Passing Clients list from expernal screen if called from any of Client browsers
        if (params.containsKey("CLIENTS")) {
            Collection clients = (Collection) params.get("CLIENTS");
            for (Object client : clients) {
                if (client instanceof Client) {
                    selectedClientsDs.addItem((Client) client);
                }
            }
        }

        clientsTokensGB.setVisible(true);
        getDialogOptions().setWidth((float)1080);
    }

    @Override
    protected boolean preCommit() {
        final Collection<Client> clients = selectedClientsDs.getItems();
        final Distribution distribution = distributionDs.getItem();

        if (distribution == null) {
            showMessageDialog("Error", "No distribution", MessageType.WARNING);
            return false;
        }

        if (clientsTokensGB.isVisible() && (clients == null || clients.isEmpty())) {
            showMessageDialog("Alert", "No emails selected!", MessageType.WARNING);
            return false;
        }

        clients.forEach(client -> {
            final DistributionSubscription subscription = metadata.create(DistributionSubscription.class);
            subscription.setClient(client);
            subscription.setDistribution(distribution);
            subscription.setStatus(SubscriptionStatus.subscribed);
            subscription.setDateBegin(new Date());
            subscription.setDateStatusUpdate(new Date());
            distributionSubscriptionsDs.addItem(subscription);
        });

        return true;
    }


//    @Override
//    protected boolean postCommit(boolean committed, boolean close) {
//        final Distribution distribution = distributionDs.getItem();
//        final List<String> emails = selectedClientsDs.getItems().stream().map(Client::getEmail).collect(toList());
//        final MailMessage message = new MailMessage()
//                .withSubject(distribution.getSubject())
//                .withBody(distribution.getContent())
//                .withImportance(distribution.getImportant() != null && distribution.getImportant())
//                .withMessageId(distribution.getId().toString());
//        try {
//            massMailingService.send(message, emails);
//        } catch (MassMailingService.MassMailingServiceException ex) {
//            showNotification(ex.getMessage());
//            return false;
//        }
//
//
//        return super.postCommit(committed, close);
//    }
}