/*
 * Copyright (c) 2015 ru.glavkniga.gklients.gui.elversubscription
 */
package ru.glavkniga.gklients.gui.elversubscription;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.validators.EmailValidator;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ElverSubscription;
import ru.glavkniga.gklients.entity.Riz;
import ru.glavkniga.gklients.service.DateTimeService;
import ru.glavkniga.gklients.service.GeneratorService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author LysovIA
 */

public class ElverSubscriptionEdit extends AbstractEditor<ElverSubscription> {

    @Inject
    private GeneratorService generatorService;

    @Named("emailField")
    Field emailField;

    @Named("nameField")
    Field nameField;

    @Named("itnField")
    Field itnField;

    @Named("elverSubscriptionDs")
    protected Datasource<ElverSubscription> elverSubscriptionDs;

    @Named("clientsCollectionDs")
    protected CollectionDatasource<Client, UUID> clientsCollectionDs;


    @Named("isRegKeyUsed")
    CheckBox isRegKeyUsed;

    @Named("isRegKeySentToRiz")
    CheckBox isRegKeySentToRiz;

    @Named("isPassSentToCustomer")
    CheckBox isPassSentToCustomer;

    @Named("isTarifChecked")
    CheckBox isTarifChecked;

    @Named("regkeyGenerateBtn")
    LinkButton regkeyGenerateBtn;

    @Inject
    private DateTimeService dts;

    private Boolean ricChangeFlag = true;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        emailField.addValueChangeListener(e -> {
            Client client = getItem().getClient();
            if (client != null && ricChangeFlag) {
                Riz riz = getRizFromLastElverSubscription(client.getId());
                getItem().setRiz(riz);
            }
        });
    }


    @Override
    public void initNewItem(ElverSubscription item) {
        emailField.setEnabled(true);
        nameField.setEnabled(true);
        itnField.setEnabled(true);

        String key = generatorService.generateKey("");
        item.setRegkey(key);
        item.setRequestDate(dts.getCurrentDateTime());

        isTarifChecked.setVisible(false);
        isRegKeyUsed.setVisible(false);
        isRegKeySentToRiz.setVisible(false);
        isPassSentToCustomer.setVisible(false);
        regkeyGenerateBtn.setVisible(true);
    }

    public void onRegkeygeneratebtnClick(Component source) {
        ElverSubscription item = elverSubscriptionDs.getItem();
        String email = "";
        if (item.getClient() != null) {
            email = item.getClient().getEmail();
        }
        String key = generatorService.generateKey(email);
        item.setRegkey(key);
    }

    @Inject
    private Metadata metadata;

    public void onNewOptionAdd(LookupField field, String caption) {
        if (clientsCollectionDs.getItem() == null) {
            caption = caption.toLowerCase();
            try {
                new EmailValidator().validate(caption);

                Client client = metadata.create(Client.class);
                client.setEmail(caption);
                getItem().setClient(client);
                String pass = generatorService.generatePass(caption);
                client.setPassword(pass);
                clientsCollectionDs.addItem(client);


            } catch (ValidationException e) {
                showMessageDialog(
                        getMessage("email_incorrect_title"),
                        getMessage("email_incorrect"),
                        MessageType.WARNING);
            }
        }

    }

    public void onUnlockButtonClick(Component source) {
        emailField.setEnabled(true);
        nameField.setEnabled(true);
        itnField.setEnabled(true);
        ricChangeFlag = false;
    }

    @Override
    protected boolean preCommit() {

        long timeStart = 0;
        long timeEnd = 0;
        ElverSubscription item = getItem();
        try {
            Date date_start = item.getIssueStart().getIssuePlannedDate();
            Date date_end = item.getIssueEnd().getIssuePlannedDate();
            timeStart = date_start.getTime();
            timeEnd = date_end.getTime();
        } catch (NullPointerException e) {
            showMessageDialog(
                    getMessage("dateFailCap"),
                    getMessage("dateSetMsg"),
                    MessageType.WARNING
            );
            return false;
        }

        if (timeStart > timeEnd) {
            showMessageDialog(
                    getMessage("dateFailCap"),
                    getMessage("dateFailMsg"),
                    MessageType.WARNING
            );
            return false;

        } else {
            if (timeStart == timeEnd) {
                try {
                    String code_start = item.getIssueStart().getCode();
                    int number_start = Integer.parseInt(code_start.substring(8, 10));

                    String code_end = item.getIssueEnd().getCode();
                    int number_end = Integer.parseInt(code_end.substring(8, 10));

                    if (number_start > number_end) {
                        showMessageDialog(
                                getMessage("dateFailCap"),
                                getMessage("dateFailMsg"),
                                MessageType.WARNING
                        );
                        return false;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        }

           /* if (isItemHasDuplicatedIssues()){
                showMessageDialog(
                        getMessage("dateFailCap"),
                        getMessage("duplicateMsg"),
                        MessageType.WARNING
                );
                return false;
            }*/
        return true;
    }

//    @Inject
//    DefineIssuesRangeService defineIssuesRangeService;

    @Named("clientSubscriptions")
    CollectionDatasource<ElverSubscription, UUID> clientSubscriptions;

    private Riz getRizFromLastElverSubscription(UUID clientId) {
        Riz riz = null;
        clientSubscriptions.refresh(ParamsMap.of("client", clientId));
        if (clientSubscriptions.size() > 0) {
            ElverSubscription item = clientSubscriptions.getItems().iterator().next();
            if (item != null) {
                riz = item.getRiz();
            }
        }
        return riz;
    }


//    private boolean isItemHasDuplicatedIssues() {
//        clientSubscriptions.refresh();
//        if (clientSubscriptions.size() > 0) {
//            ElverSubscription item = elverSubscriptionDs.getItem();
//            HashSet<UUID> itemIssues = new HashSet<>();
//            if (item != null) {
//                List<Schedule> list = defineIssuesRangeService.getMagazineIssuesForES(item);
//                if (list.size() > 0) {
//                    for (Schedule schedule : list) {
//                        itemIssues.add(schedule.getMagazineIssue().getId());
//                    }
//                }
//            }
//            HashSet<UUID> otherItemIssues = new HashSet<>();
//            for (ElverSubscription anotherSub : clientSubscriptions.getItems()) {
//                List<Schedule> list = defineIssuesRangeService.getMagazineIssuesForES(anotherSub);
//                if (list.size() > 0) {
//                    for (Schedule schedule : list) {
//                        otherItemIssues.add(schedule.getMagazineIssue().getId());
//                    }
//                }
//            }
//            for (UUID issueId : itemIssues) {
//                if (otherItemIssues.contains(issueId)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
}