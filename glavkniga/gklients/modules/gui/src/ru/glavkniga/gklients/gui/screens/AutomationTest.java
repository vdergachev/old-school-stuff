/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.gui.screens;

import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ElverSubscription;
import ru.glavkniga.gklients.service.GKExchangeService;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author LysovIA
 */
public class AutomationTest extends AbstractWindow {

    @Named("esDs")
    CollectionDatasource<ElverSubscription,UUID> eDs;

    @Named("userDs")
    CollectionDatasource<Client,UUID> uDs;

    @Named("regkeyField")
    LookupField regkeyField;

    @Named("userField")
    LookupField userField;


    @Inject
    protected GKExchangeService gkExchangeService;

    @Inject
    protected ReportGuiManager reportGuiManager;
    @Inject
    protected UserSessionSource userSessionSource;



    @Override
    public void init(Map<String, Object> params) {

        uDs.refresh();
        eDs.refresh();

        List<String> usersList = new ArrayList<>();

        for(Client client : uDs.getItems()){
            usersList.add(client.getEmail());
        }
        userField.setOptionsList(usersList);

        List<String> optionsList = new ArrayList<>();
        for(ElverSubscription elverSubscription: eDs.getItems()){
            optionsList.add(elverSubscription.getRegkey());
        }
        regkeyField.setOptionsList(optionsList);
    }

    public void onIsuserbtnClick(Component source) {
        String email = userField.getValue();
        if (email != null) {
            String result = gkExchangeService.isUser(email)+"";
            showNotification(result, NotificationType.HUMANIZED);
        } else {
            showNotification("Select user!", NotificationType.HUMANIZED);
        }
    }

    public void onIsregkeybtnClick(Component source) {
        String regkey = regkeyField.getValue();
        String result = "";
        if (regkey !=null) {
            Date activationDate =gkExchangeService.isRegkey(regkey);
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            try {
                 result = df.format(activationDate);
            }
            catch (NullPointerException e){
                result = "false";
            }
            showNotification(result, NotificationType.HUMANIZED);
        } else {
            showNotification("Select regkey!", NotificationType.HUMANIZED);
        }
    }

    public void onCountusersbtnClick(Component source) {
        String response = gkExchangeService.countUsers() + "";
        showMessageDialog("countUsers", response, MessageType.WARNING);
    }


    public void onCountregkeysbtnClick(Component source) {
        String response = gkExchangeService.countRegkeys() + "";
        showMessageDialog("countUsers", response, MessageType.WARNING);
    }

    public void onSetregkeysClick(Component source) {
        List<Report> reportCollection = reportGuiManager.getAvailableReports(
                null,
                userSessionSource.getUserSession().getUser(),
                null
        );
        if (reportCollection.size() > 0) {
            for (Report report : reportCollection) {
                if (report.getName().equals("regkeys_report")) {
                    int numRows = gkExchangeService.setRegkeys(report);
                    showNotification(numRows+" rows imported", NotificationType.HUMANIZED);
                }
            }
        }

    }

    public void onSetusersClick(Component source) {
        List<Report> reportCollection = reportGuiManager.getAvailableReports(
                null,
                userSessionSource.getUserSession().getUser(),
                null
        );
        if (reportCollection.size() > 0) {
            for (Report report : reportCollection) {
                if (report.getName().equals("services_report")) {
                    int numRows = gkExchangeService.setUsers(report);
                    showNotification(numRows+" rows imported", NotificationType.HUMANIZED);
                }
            }
        }
    }

    public void onSetscheduleClick(Component source) {
        List<Report> reportCollection = reportGuiManager.getAvailableReports(
                null,
                userSessionSource.getUserSession().getUser(),
                null
        );
        if (reportCollection.size() > 0) {
            for (Report report : reportCollection) {
                if (report.getName().equals("schedule_report")) {
                    int numRows = gkExchangeService.setSchedule(report);
                    showNotification(numRows+" rows imported", NotificationType.HUMANIZED);
                }
            }
        }
    }


    public void onCountTestClick(Component source) {
        String response = gkExchangeService.countTest() + "";
        showMessageDialog("countTest", response, MessageType.WARNING);
    }

    public void onCountEmailClick(Component source) {
        String response = gkExchangeService.countTestEmail() + "";
        showMessageDialog("countTestEmail", response, MessageType.WARNING);
    }

    public void onCountTestMarkClick(Component source) {
        String response = gkExchangeService.countTestMark() + "";
        showMessageDialog("countTestMark", response, MessageType.WARNING);
    }


    public void onGetTestClick(Component source) {
        String response = gkExchangeService.getTest().size() + "";
        showMessageDialog("countTest", response, MessageType.WARNING);
    }

    public void onGetEmailClick(Component source) {

        String response = gkExchangeService.getTestEmail().size() + "";
        showMessageDialog("countTest", response, MessageType.WARNING);
    }

    public void onGetTestMarkClick(Component source) {
        String response = gkExchangeService.getTestMark().size() + "";
        showMessageDialog("countTest", response, MessageType.WARNING);
    }

    public void onBtncloseClick(Component source) {
        this.close(Window.CLOSE_ACTION_ID);
    }


}