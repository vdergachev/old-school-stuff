/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.client;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import ru.glavkniga.gklients.entity.Client;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

/**
 * @author LysovIA
 */
public class ClientFailedEmailBrowse extends AbstractLookup {

    @Named("clientsTable")
    private Table<Client> clientsTable;

    @Named("clientsDs")
    private CollectionDatasource<Client, UUID> clientsDs;

    @Named("btnMarkAsBad")
    private Button btnMarkAsBad;

    @Override
    public void init(Map<String, Object> params) {
        clientsDs.addItemChangeListener(e -> {
            Client client =  clientsDs.getItem();
            if (client != null){
                btnMarkAsBad.setEnabled(true);
                if (client.getBadEmail() != null && client.getBadEmail()){
                    btnMarkAsBad.setCaption(getMessage("btnMarkAsGoodCap"));
                }else{
                    btnMarkAsBad.setCaption(getMessage("btnMarkAsBadCap"));
                }
            } else {
                btnMarkAsBad.setEnabled(false);
            }
        });
    }

    public void onBtnMarkAsBadClick() {
        Client client = clientsTable.getSingleSelected();
        if (client != null) {
            if (client.getBadEmail() != null && client.getBadEmail()){
                btnMarkAsBad.setCaption(getMessage("btnMarkAsBadCap"));
                client.setBadEmail(false);
            }else{
                btnMarkAsBad.setCaption(getMessage("btnMarkAsGoodCap"));
                client.setBadEmail(true);
            }
            clientsDs.commit();
        }
    }
}