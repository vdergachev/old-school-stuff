/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.distributionsubscription;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import ru.glavkniga.gklients.entity.DistributionSubscription;

import javax.inject.Named;
import java.util.UUID;

/**
 * @author Igor Lysov
 */
public class DistributionSubscriptionEdit extends AbstractEditor<DistributionSubscription> {

    @Named("clientGb")
    private GroupBoxLayout clientGb;

    @Named("distributionSubscriptionDs")
    private Datasource<DistributionSubscription> distributionSubscriptionDs;

    @Named("clientSubscriptionsDs")
    private CollectionDatasource<DistributionSubscription, UUID> clientSubscriptionsDs;

    @Override
    protected void postInit() {
        DistributionSubscription item = distributionSubscriptionDs.getItem();
        if (item != null && item.getClient() != null)
            clientGb.setVisible(false);
    }

    @Override
    protected boolean preCommit() {
        clientSubscriptionsDs.refresh();
        if (clientSubscriptionsDs.size() > 0) {
            showNotification(getMessage("distributionSubscriptionExistMsg"));
            return false;
        } else
            return true;
    }
}