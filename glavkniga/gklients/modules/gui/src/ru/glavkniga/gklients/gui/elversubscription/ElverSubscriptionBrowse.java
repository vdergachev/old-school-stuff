/*
 * Copyright (c) 2015 ru.glavkniga.gklients.gui.elvercustomer
 */
package ru.glavkniga.gklients.gui.elversubscription;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.filter.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.filter.FilterDelegate;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.EntityLogItem;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ElverSubscription;
import ru.glavkniga.gklients.entity.OnetimeMailing;
import ru.glavkniga.gklients.service.EmailerService;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * @author LysovIA
 */
public class ElverSubscriptionBrowse extends AbstractLookup {

    private Map filterParam;

    @Inject
    private Filter filter;

    @Inject
    private EmailerService emailerService;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private TimeSource timeSource;

    @Inject
    private Metadata metadata;

    @Named("logDs")
    CollectionDatasource<EntityLogItem, UUID> logDs;

    @Named("elverSubscriptionsTable")
    GroupTable<ElverSubscription> elverSubscriptionsTable;

    @Named("elverSubscriptionsDs")
    CollectionDatasource<ElverSubscription, UUID> elverSubscriptionsDs;

    @Named("multiselectBtn")
    private PopupButton multiselectBtn;

    @Named("elverSubsSingleDS")
    protected Datasource<ElverSubscription> elverSubsSingleDS;

    @Named("blockBtn")
    private Button blockButton;

    @Named("flagBtn")
    private PopupButton flagBtn;

    @Named("clientDs")
    protected Datasource<Client> clientDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Object filterMap = params.get("FILTER");
        if (filterMap != null && filterMap instanceof Map) {
            filterParam = (Map) filterMap;
        }

        logDs.refresh();
        elverSubscriptionsDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                multiselectBtn.setEnabled(true);
                flagBtn.setEnabled(true);
                ElverSubscription reloadedItem = elverSubscriptionsDs.getItem();
                Client client = reloadedItem.getClient();
                if (client != null) {
                    blockButton.setEnabled(true);
                    setButtonCaption(client.getIsBlocked());
                }
            } else {
                blockButton.setEnabled(false);
                flagBtn.setEnabled(false);
                multiselectBtn.setEnabled(false);
            }
        });

        elverSubscriptionsDs.addCollectionChangeListener(e -> {
            // elverSubscriptionsDs.refresh();
            logDs.refresh();
        });

        CustomColumnGenerator regkeyRizGenerator = new CustomColumnGenerator(
                logDs.getItems(), "isRegKeySentToRiz", componentsFactory, getMessage("neverChanged"));
        elverSubscriptionsTable.addGeneratedColumn("isRegKeySentToRizCustom", regkeyRizGenerator);

        CustomColumnGenerator regkeyUsedGenerator = new CustomColumnGenerator(
                logDs.getItems(), "isRegKeyUsed", componentsFactory, getMessage("neverChanged"));
        elverSubscriptionsTable.addGeneratedColumn("isRegkeyUsedCustom", regkeyUsedGenerator);

        CustomColumnGenerator passSentToCustomerGenerator = new CustomColumnGenerator(
                logDs.getItems(), "isPassSentToCustomer", componentsFactory, getMessage("neverChanged"));
        elverSubscriptionsTable.addGeneratedColumn("isPassSentToCustomerCustom", passSentToCustomerGenerator);

        CustomColumnGenerator tarifCheckedCustomGenerator = new CustomColumnGenerator(
                logDs.getItems(), "isTarifChecked", componentsFactory, getMessage("neverChanged"));
        elverSubscriptionsTable.addGeneratedColumn("isTarifCheckedCustom", tarifCheckedCustomGenerator);


        elverSubscriptionsTable.getColumn("isRegkeyUsedCustom").setCaption(getMessage("regUsedCpn"));
        elverSubscriptionsTable.getColumn("isRegKeySentToRizCustom").setCaption(getMessage("regSentCpn"));
        elverSubscriptionsTable.getColumn("isPassSentToCustomerCustom").setCaption(getMessage("passSentCpn"));
        elverSubscriptionsTable.getColumn("isTarifCheckedCustom").setCaption(getMessage("tariffCheckedCpn"));

    }

    @Override
    @SuppressWarnings("unchecked")
    public void ready() {
        if (filterParam != null && filterParam.size() >0){
            filterParam.forEach((paramName, paramValue) -> {
                if (paramName != null ){
                    String queryString = "e."+paramName.toString()+" = :custom$paramValue";
                    QueryFilter queryFilter = new QueryFilter(new Clause("", queryString, null, null, null));
                    elverSubscriptionsDs.setQueryFilter(queryFilter);
                    elverSubscriptionsDs.refresh(ParamsMap.of("paramValue", paramValue));
                    filter.setEnabled(false);
                }
            });
        }
    }

    public void activatePerform(Component source) {
        ElverSubscription es = elverSubscriptionsTable.getSingleSelected();
        if (es != null && es.getActivation_date() == null) {
            showOptionDialog(
                    getMessage("msConfirm"),
                    getMessage("msActivationRemind"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    ElverSubscription es = elverSubscriptionsTable.getSingleSelected();
                                    es.setActivation_date(timeSource.currentTimestamp());
                                    es.setIsPassSentToCustomer(true);
                                    elverSubscriptionsDs.commit();
                                    elverSubscriptionsDs.refresh();
                                    emailerService.activateRegkey(es.getId());
                                }
                            },
                            new DialogAction(DialogAction.Type.NO) {
                            }
                    }
            );
        } else {
            showMessageDialog(
                    getMessage("msActivationError"),
                    getMessage("msActivationComplete"),
                    MessageType.WARNING);
        }
    }

    public void keyEnteredAction(Component source) {
        setFlag("setIsRegKeyUsed");
    }

    public void keySentToRizAction(Component source) {
        setFlag("setIsRegKeySentToRiz");
    }

    public void tariffCheckedAction(Component source) {
        setFlag("setIsTarifChecked");
    }

    public void passSentAction(Component source) {
        setFlag("setIsPassSentToCustomer");
    }

    private void setFlag(String methodName) {
        Set<ElverSubscription> subscriptions = elverSubscriptionsTable.getSelected();
        showOptionDialog(
                getMessage("msConfirm"),
                getMessage("msFlagSetConfirm"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {

                                for (ElverSubscription subs : subscriptions) {
                                    java.lang.reflect.Method method;
                                    try {
                                        method = ElverSubscription.class.getMethod(methodName, Boolean.class);
                                        try {
                                            method.invoke(subs, TRUE);
                                        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (SecurityException | NoSuchMethodException e) {
                                        e.printStackTrace();
                                    }
                                    elverSubsSingleDS.setItem(subs);
                                    elverSubsSingleDS.commit();
                                }
                                elverSubscriptionsDs.refresh();
                            }
                        },
                        new DialogAction(DialogAction.Type.NO) {
                        }
                }
        );
    }

    public void onBlockBtnClick() {
        ElverSubscription es = elverSubscriptionsTable.getSingleSelected();
        if (elverSubscriptionsTable.getSelected().size() > 1) {
            showNotification(
                    getMessage("errorSelectMsg"),
                    NotificationType.ERROR);
            return;
        }
        Client client = es.getClient();
        Boolean blocked;
        if (client.getIsBlocked() == null || !client.getIsBlocked())
            blocked = FALSE;
        else
            blocked = TRUE;
        Boolean toBlock = !blocked;
        String message;
        if (toBlock)
            message = getMessage("msBlockingRemind");
        else
            message = getMessage("msUnBlockingRemind");
        showOptionDialog(
                getMessage("msConfirm"),
                message,
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                client.setIsBlocked(toBlock);
                                setButtonCaption(toBlock);
                                if (toBlock)
                                    showNotification(
                                            getMessage("blockedMsg"),
                                            NotificationType.HUMANIZED);
                                else
                                    showNotification(
                                            getMessage("unblockedMsg"),
                                            NotificationType.HUMANIZED);
                                clientDs.setItem(client);
                                clientDs.commit();
                                elverSubscriptionsDs.refresh();
                            }
                        },
                        new DialogAction(DialogAction.Type.NO) {
                        }
                }
        );
    }

    private void setButtonCaption(Boolean isBlocked) {
        if (isBlocked == null || !isBlocked)
            blockButton.setCaption(getMessage("blockBtnCap"));
        else
            blockButton.setCaption(getMessage("unblockBtnCap"));
    }

    public void onMakeOntimeSending(Component source) {
        Set<ElverSubscription> subscriptions = elverSubscriptionsTable.getSelected();
        Set<Client> clients = new HashSet<>();

        if (subscriptions != null) {
            subscriptions.forEach(elverSubscription -> clients.add(elverSubscription.getClient()));
            Map<String, Object> params = new HashMap<>();
            OnetimeMailing mailing = metadata.create(OnetimeMailing.class);
            params.put("CLIENTS", clients);
            openEditor(mailing, WindowManager.OpenType.DIALOG, params);

        }
    }

    public void onSubscribeToDistribution(Component source) {
        Set<ElverSubscription> subscriptions = elverSubscriptionsTable.getSelected();
        Set<Client> clients = new HashSet<>();

        if (subscriptions != null) {
            subscriptions.forEach(elverSubscription -> clients.add(elverSubscription.getClient()));
            Map<String, Object> params = new HashMap<>();
            params.put("CLIENTS", clients);
            openWindow("mass-distribution-subscription-add", WindowManager.OpenType.DIALOG.height(300).width(750), params);
        }

    }
}