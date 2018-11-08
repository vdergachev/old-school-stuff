/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.distributionSubscriptionComposition;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.validators.EmailValidator;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientDistributionSettings;
import ru.glavkniga.gklients.entity.Distribution;
import ru.glavkniga.gklients.entity.DistributionSubscription;
import ru.glavkniga.gklients.enumeration.SubscriptionStatus;
import ru.glavkniga.gklients.service.DateTimeService;
import ru.glavkniga.gklients.service.GeneratorService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.UUID;

/**
 * @author Igor Lysov
 */
public class ClientWithSettingsEdit extends AbstractEditor<ClientDistributionSettings> {

    @Named("distributionSubscriptionDs")
    private Datasource<DistributionSubscription> distributionSubscriptionDs;

    @Named("distField")
    private LookupField distField;

    @Named("allClientsDs")
    private CollectionDatasource<Client, UUID> allClientsDs;

    @Named("allSettingsForClient")
    private CollectionDatasource<ClientDistributionSettings, UUID> allSettingsForClient;

    @Named("clientDistributionSettingsDs")
    private Datasource<ClientDistributionSettings> clientDistributionSettingsDs;

    @Named("distributionsDs")
    private CollectionDatasource<Distribution, UUID> distributionsDs;

    @Named("distGroupBox")
    private GroupBoxLayout distGroupBox;

    @Named("nameField")
    Field nameField;

    @Named("itnField")
    Field itnField;

    @Inject
    private Metadata metadata;

    @Inject
    private GeneratorService generatorService;

    @Inject
    private DateTimeService dts;

    private Boolean isNew=false; //TODO replace with New / Detached identification of getItem() value


    public void onNewOptionAdd(LookupField field, String caption) {
        if (allClientsDs.getItem() == null) {
            caption = caption.toLowerCase();
            try {
                new EmailValidator().validate(caption);

                Client client = metadata.create(Client.class);
                client.setEmail(caption);
                getItem().setClient(client);
                String pass = generatorService.generatePass(caption);
                client.setPassword(pass);
                allClientsDs.addItem(client);

            } catch (ValidationException e) {
                showMessageDialog(
                        getMessage("email_incorrect_title"),
                        getMessage("email_incorrect"),
                        MessageType.WARNING);
            }
        }

    }


    @Override
    protected void initNewItem(ClientDistributionSettings item) {
        super.initNewItem(item);
        Client client = metadata.create(Client.class);
        item.setClient(client);
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
        client.setClientDistributionSettings(item);

        distGroupBox.setVisible(true);
        nameField.setEnabled(true);
        itnField.setEnabled(true);

        distField.addValueChangeListener(e -> {
            DistributionSubscription newDS = metadata.create(DistributionSubscription.class);
            newDS.setClient(item.getClient());
            newDS.setDistribution(distributionsDs.getItem());
            newDS.setStatus(SubscriptionStatus.subscribed);
            newDS.setDateBegin(dts.getCurrentDateTime());
            newDS.setDateStatusUpdate(dts.getCurrentDateTime());
            distributionSubscriptionDs.setItem(newDS);
        });

        this.isNew = true;
    }

    @Override
    protected boolean preCommit() {
        allSettingsForClient.refresh();
        if (allSettingsForClient.size() > 0 && this.isNew) {
         showNotification(getMessage("alreadyExist"));
         return false;
        }
        return true;
    }
}