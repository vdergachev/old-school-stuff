/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.onetimemailing;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.AddAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import ru.glavkniga.gklients.entity.*;
import ru.glavkniga.gklients.enumeration.OnetimeMailingStatus;
import ru.glavkniga.gklients.mailing.TemplateProcessor;
import ru.glavkniga.gklients.service.TemplateProcessService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * @author Igor Lysov
 */
public class OnetimeMailingEdit extends AbstractEditor<OnetimeMailing> {

    @Named("externalClientsDs")
    private CollectionDatasource<Client, UUID> externalClientsDs;

    @Named("tokensTable")
    private Table<Token> tokensTable;

    @Named("onetimeMailingDs")
    private Datasource<OnetimeMailing> onetimeMailingDs;

    @Named("attachmentsPopup")
    private PopupView attachmentsPopup;

    @Named("attachmentsTable.add")
    private AddAction attachmentsTableAdd;

    @Named("attachmentsTable.remove")
    private RemoveAction attachmentsTableRemove;

    @Named("bodyField")
    private RichTextArea bodyField;

    @Inject
    private TemplateProcessService processor;

    @Named("labelPreview")
    private Label labelPreview;

    @Named("previewPopup")
    private PopupView previewPopup;

    private String messageBody = "";

    @Inject
    private Metadata metadata;

    @Named("mailingStatisticsDs")
    private Datasource<MailingStatistics> mailingStatisticsDs;

    @Named("statusField")
    private LookupField statusField;

    @Named("plannedDateField")
    private DateField plannedDateField;


    @Override
    public void init(Map<String, Object> params) {
        Table.CellClickListener clickListener = (item, columnId) -> {
            Token token = (Token) item;
            messageBody += "${" + token.getToken() + "|\"\"}";
            bodyField.setValue(messageBody);
        };


        tokensTable.setClickListener("token", clickListener);

        //Passing Clients list from expernal screen if called from any of Client browsers
        if (params.containsKey("CLIENTS")) {
            Collection clients = (Collection) params.get("CLIENTS");
            for (Object client : clients) {
                if (client instanceof Client) {
                    externalClientsDs.addItem((Client) client);
                }
            }
        }

        // Add handler to display attachments
        attachmentsTableAdd.setBeforeActionPerformedHandler(() -> {
            attachmentsPopup.setPopupVisible(false);
            return true;
        });

        attachmentsTableAdd.setAfterAddHandler(items -> updateAttachmentCount());

        attachmentsTableRemove.setAfterRemoveHandler(items -> updateAttachmentCount());

        bodyField.addValueChangeListener(e ->
        {
            String body = bodyField.getValue();
            if (body != null)
                this.messageBody = body;
            else this.messageBody = "";
        });

        // Add email preview field
        previewPopup.addPopupVisibilityListener(e -> {
            if (previewPopup.isPopupVisible()) {
                OnetimeMailing item = getItem();


                EmailTemplate template = item.getTemplate();
                Set<Client> clients = getItem().getClient();
                if (messageBody != null && !messageBody.isEmpty() && clients != null && !clients.isEmpty()) {
                    Client client = clients.iterator().next();
                    String emailBody = messageBody;
                    if (template != null) {
                        String templateBody = template.getBody();
                        emailBody = templateBody.replace("[CONTENT]", messageBody);
                    }
                    String htmlContent = null;
                    if (emailBody.contains("${")) {
                        try {
                            htmlContent = processor.processTemplate(emailBody, client.getId());
                        } catch (TemplateProcessor.TemplateProcessorException ex) {
                            labelPreview.setValue("<p>No data to show</p>");
                            showNotification(ex.getLocalizedMessage());
                        } catch (Exception ex) {
                            labelPreview.setValue("<p>No data to show</p>");
                            showNotification(getMessage("noDataForTokens"));
                        }
                    } else htmlContent = emailBody;

                    labelPreview.setValue(htmlContent);
                } else {
                    labelPreview.setValue("<p>No data to show</p>");
                    showNotification(getMessage("clientNotSet"));
                }
            }
        });

        // Add options in statusField


        Map<String, OnetimeMailingStatus> optionsMap = new LinkedHashMap<>();

        String statusNew = messages.getMessage(OnetimeMailingStatus.class, "New");
        String statusPlanned = messages.getMessage(OnetimeMailingStatus.class, "Planned");

        optionsMap.put(statusNew, OnetimeMailingStatus.New);
        optionsMap.put(statusPlanned, OnetimeMailingStatus.Planned);
        statusField.setOptionsMap(optionsMap);
    }

    @Inject
    private Messages messages;


    private void updateAttachmentCount() {
        if (onetimeMailingDs.getItem() != null) {
            OnetimeMailing item = onetimeMailingDs.getItem();
            if (item.getAttachments() != null)
                attachmentsPopup.setMinimizedValue(getMessage("attachmentsPopupCap") + " (" + String.valueOf(item.getAttachments().size()) + ")");
            else
                attachmentsPopup.setMinimizedValue(getMessage("attachmentsPopupCap") + " (0)");
        }
    }

    private void lockStatusField() {
        if (onetimeMailingDs.getItem() != null) {
            OnetimeMailing item = onetimeMailingDs.getItem();
            OnetimeMailingStatus status = item.getStatus();
            switch (status) {
                case New:
                case Planned:
                case Error:
                    statusField.setEnabled(true);
                    plannedDateField.setEnabled(true);
                    break;
                case Sending:
                case Queued:
                case Completed:
                    statusField.setEnabled(false);
                    plannedDateField.setEnabled(false);
                    break;
            }
        }
    }

    @Override
    protected void postInit() {
        super.postInit();
        this.updateAttachmentCount();
        this.lockStatusField();
    }

    @Override
    protected void initNewItem(OnetimeMailing item) {
        item.setSendingDate(new Date());
        item.setStatus(OnetimeMailingStatus.New);
        Collection<Client> clients = externalClientsDs.getItems();

        if (clients != null && clients.size() > 0) {
            item.setClient(new HashSet<>(clients));
        }

        MailingStatistics statistics = metadata.create(MailingStatistics.class);
        statistics.setOnetimeMailing(item);
        item.setMailingStatistics(statistics);
        mailingStatisticsDs.setItem(statistics);
    }

    @Override
    protected boolean preCommit() {
        OnetimeMailing item = getItem();
        Set<Client> clients = item.getClient();
        if (item.getStatus() != OnetimeMailingStatus.New && item.getStatus() != OnetimeMailingStatus.Planned) {
            showMessageDialog(getMessage("cantSaveMsg"), getMessage("mailingStatusDoesntAllowSaving"), MessageType.WARNING);
            return false;
        }

        if (clients == null || clients.isEmpty()) {
            showMessageDialog(getMessage("cantSaveMsg"), getMessage("noEmailsSelectedMsg"), MessageType.WARNING);
            return false;
        }

        if (item.getBody().isEmpty() && item.getStatus() == OnetimeMailingStatus.Planned) {
            showMessageDialog(getMessage("cantSaveMsg"), getMessage("clientNotSet"), MessageType.WARNING);
            return false;
        }


        EmailTemplate template = item.getTemplate();
        if (template != null) {
            String templateBody = template.getBody();
            if (!templateBody.contains("[CONTENT]")) {
                showMessageDialog(getMessage("cantSaveMsg"), getMessage("templateWrongMsg"), MessageType.WARNING);
                return false;
            }
        }

        if (item.getSubject().contains("${")) {
            showMessageDialog(getMessage("cantSaveMsg"), getMessage("tokensInSubjectNotSupported"), MessageType.WARNING);
            return false;
        }

        if (item.getBody().contains("${")) {
            item.setPersonal(true);
        }

        MailingStatistics statistics = item.getMailingStatistics();
        if (statistics == null) {
            statistics = metadata.create(MailingStatistics.class);
            statistics.setOnetimeMailing(item);
            item.setMailingStatistics(statistics);
        }
        statistics.setPlanned(clients.size());
        statistics.setSending(0);
        statistics.setCompleted(0);
        statistics.setFailed(0);

        StringBuilder clientsFailed = new StringBuilder();
        if (item.getStatus() == OnetimeMailingStatus.Planned) {
            for (Client client : clients) {
                try {
                    processor.processTemplate(messageBody, client.getId());
                } catch (Exception ex) {
                    clientsFailed.append(client.getEmail())
                            .append(", ");
                }
            }
            if (!clientsFailed.toString().isEmpty()) {
                showMessageDialog(getMessage("cantSaveMsg"), getMessage("tokensNotProcessed") + clientsFailed.toString(), MessageType.WARNING);
                return false;
            }
        }
        return super.preCommit();
    }


//    public void onValidateBtnClick() {
    // this has to show which tokens crashed
//        Set<Client> clients = getItem().getClient();
//        if (clients != null && !clients.isEmpty()) {
//            boolean result = false;
//            try {
//                result = processor.validateTemplate(this.messageBody);
//            } catch (TemplateProcessor.TemplateProcessorException ex) {
//                StringBuilder message = new StringBuilder("Wrong tokens found: \n");
//
//                for (String token : ex.getWrongTokens()) {
//                    message.append(token).append(", ");
//                }
//                showNotification(message.toString());
//            }
//            showNotification("Validator returned " + String.valueOf(result));
//
//
//
//        } else {
//            showNotification("No emails selected!");
//        }
//
//    }
}