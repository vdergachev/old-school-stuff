/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.client;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.jetbrains.annotations.NotNull;
import ru.glavkniga.gklients.entity.Client;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Хомяк
 */
public class ClientWithServices extends AbstractLookup {

    @Named("clientsTable")
    private GroupTable<Client> clientsTable;

    @Named("clientsDs")
    private GroupDatasource<Client, UUID> clientsDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    public void init(Map<String, Object> params) {

        clientsTable.addGeneratedColumn("elverSubscriptions", client -> {
            LinkButton linkButton = makeButton(client.getElverSubscription());
            if (!linkButton.getCaption().isEmpty()) {
                linkButton.setAction(new BaseAction("elver-" + String.valueOf(client.getId())) {
                    @Override
                    public void actionPerform(Component component) {
                        Map<String, Object> elverParams = getParamsMap(client);
                        openWindow("gklients$ElverSubscription.lookup", WindowManager.OpenType.THIS_TAB, elverParams);
                    }
                });
            }
            return linkButton;
        });

        clientsTable.addGeneratedColumn("distributionSubscriptions", client -> {
            LinkButton linkButton = makeButton(client.getDistributionSubscription());
            if (!linkButton.getCaption().isEmpty()) {
                linkButton.setAction(new BaseAction("distribution-" + String.valueOf(client.getId())) {
                    @Override
                    public void actionPerform(Component component) {
                        Map<String, Object> distrParams = getParamsMap(client);
                        openWindow("gklients$DistributionSubscription.browse", WindowManager.OpenType.THIS_TAB, distrParams);
                    }
                });
            }
            return linkButton;
        });
    }

    @NotNull
    private Map<String, Object> getParamsMap(Client client) {
        Map<String, Object> elverParams = new HashMap<>();
        Map<String, Object> filterParams = new HashMap<>();
        filterParams.put("client.id", client);
        elverParams.put("FILTER", filterParams);
        return elverParams;
    }

    private LinkButton makeButton(List<? extends Entity> entityList) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        if (entityList != null && entityList.size()>0) {
            linkButton.setCaption(String.valueOf(entityList.size()));
        } else
            linkButton.setCaption("");
        return linkButton;
    }


}