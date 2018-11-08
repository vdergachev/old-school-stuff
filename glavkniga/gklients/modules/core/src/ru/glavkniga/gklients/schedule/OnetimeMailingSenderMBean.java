/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.schedule;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.glavkniga.gklients.entity.*;
import ru.glavkniga.gklients.enumeration.AttachmentMethod;
import ru.glavkniga.gklients.enumeration.OnetimeMailingStatus;
import ru.glavkniga.gklients.mailing.MailMessage;
import ru.glavkniga.gklients.service.DateTimeService;
import ru.glavkniga.gklients.service.MailingStatisticService;
import ru.glavkniga.gklients.service.MassMailingService;
import ru.glavkniga.gklients.service.TemplateProcessService;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static ru.glavkniga.gklients.service.MassMailingService.MassMailingServiceException;

/**
 * Created by IgorLysov on 08.07.2017.
 */

@Component(OnetimeMailingSender.NAME)
public class OnetimeMailingSenderMBean implements OnetimeMailingSender {

    @Inject
    private MassMailingService massMailingService;

    @Inject
    private EmailService emailService;

    @Inject
    private TemplateProcessService processor;

    @Inject
    private Persistence persistence;

    @Inject
    private DateTimeService dts;

    @Inject
    private MailingStatisticService mailingStatisticService;

    @Inject
    private FileStorageAPI fileStorageAPI;


    @Inject
    private DataManager dm;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public String send() {
        StringBuilder result = new StringBuilder();

        //collect mailings to update statistics
        List<OnetimeMailing> statisticsList = selectOnetimeMailings(OnetimeMailingStatus.Queued);
        result.append("Statistics:\n");
        if (statisticsList != null && !statisticsList.isEmpty()) {
            String statisticsUpdateResult = updateStatistics(statisticsList);
            if (!statisticsUpdateResult.isEmpty())
                result.append(statisticsUpdateResult).append("\n");
            else
                result.append("Awaiting statistic for ")
                        .append(statisticsList.size())
                        .append(" mailing(s)\n");
        } else
            result.append("No statistics updated").append("\n");

        // collect mailings and send them
        List<OnetimeMailing> mailingList = selectOnetimeMailings(OnetimeMailingStatus.Planned);
        result.append("Mailing:\n");
        if (mailingList != null && !mailingList.isEmpty()) {
            String mailingSendResult = sendMailings(mailingList);

            result.append(mailingSendResult).append("\n");
        } else
            result.append("No emails to send now").append("\n");
        return result.toString();
    }

    private String sendMailings(List<OnetimeMailing> mailingList) {
        StringBuilder returnString = new StringBuilder();
        for (OnetimeMailing item : mailingList) {
            //set status OnetimeMailingStatus.Sending immediately as we start sending
            persistStatus(item.getId(), OnetimeMailingStatus.Sending);

            String caption = item.getSubject();
            String body = includeTemplate(item.getBody(), item.getTemplate());


            final List<FileDescriptor> attachingFiles = new ArrayList<>();
            attachingFiles.addAll(collectAttachments(item.getAttachments()));

            final EmailTemplate emailTemplate = item.getTemplate();
            if (emailTemplate != null) {
                attachingFiles.addAll(collectAttachments(emailTemplate.getAttachments()));
            }

            if (item.getPersonal() != null && item.getPersonal()) {
                // Send individual mailing
                String sendingPersonalMailingResult = sendPersonalEmails(caption, body, attachingFiles, item.getClient(), item.getId());
                returnString.append(sendingPersonalMailingResult);
            } else {
                //send mass mailing
                String sendingMassMailingResult = sendMassEmails(caption, body, attachingFiles, item.getClient(), item.getId());
                returnString.append(sendingMassMailingResult);
            }
            OnetimeMailing refreshedMailing = (OnetimeMailing) EntityWorker.getEntity(OnetimeMailing.class, item.getId());
            if (refreshedMailing.getStatus() == OnetimeMailingStatus.Sending)
                persistStatus(refreshedMailing.getId(), OnetimeMailingStatus.Queued);
        }
        return returnString.toString();
    }

    private String sendPersonalEmails(String caption, String body, List<FileDescriptor> attachingFiles, Collection<Client> clients, UUID mailingID) {
        StringBuilder returnString = new StringBuilder();
        int errorCounter = 0;
        for (Client client : clients) {
            if (client.getBadEmail()!= null && client.getBadEmail()){
                returnString.append("Skipped sending to bad email ")
                        .append(client.getEmail())
                        .append("\n");
                continue;
            }
            String email = "";
            try {
                email = client.getEmail();
                String processedBody = processor.processTemplate(body, client.getId());
                EmailInfo emailInfo = new EmailInfo(email, caption, processedBody);
                emailInfo.setAttachments(buildAttachmentsArray(attachingFiles));

                emailService.sendEmailAsync(emailInfo);
               // log.info("email sent to user "+email);

            } catch (Exception e) {
                returnString
                        .append("Exception sending personal email to ")
                        .append(email)
                        .append(" in mailing ")
                        .append(mailingID)
                        .append("\n");
                errorCounter++;
            }
        }
        if (errorCounter == clients.size()) { //if every email sent failed - we need to mark entire Mailing as failed
            persistStatus(mailingID, OnetimeMailingStatus.Error);
        }
        return returnString.toString();
    }

    private EmailAttachment[] buildAttachmentsArray(List<FileDescriptor> attachingFiles) {
        if (attachingFiles != null && attachingFiles.size() > 0) {
            int arraySize = attachingFiles.size();
            EmailAttachment[] attachments = new EmailAttachment[arraySize];
            for (int i = 0; i < attachments.length; i++) {
                FileDescriptor descriptor = attachingFiles.get(i);
                try {
                    attachments[i] = new EmailAttachment(fileStorageAPI.loadFile(descriptor), descriptor.getName());
                } catch (FileStorageException e) {
                    switch (e.getType()) {
                        case IO_EXCEPTION:
                        case FILE_ALREADY_EXISTS:
                        case FILE_NOT_FOUND:
                        case MORE_THAN_ONE_FILE:
                        case STORAGE_INACCESSIBLE:
                            attachments[i] = null;
                            break;
                    }
                }
            }
            return attachments;
        } else
            return null;
    }

    private String includeTemplate(String emailBody, EmailTemplate template) {
        String body = (template != null) ? template.getBody().replace("[CONTENT]", emailBody) : emailBody;
        if (!body.contains("<html")) {
            if (!body.contains("<body")) {
                body = "<body>" + body + "</body>";
            }
            body = "<html>" + body + "</html>";
        }
        return body;
    }

    private List<FileDescriptor> collectAttachments(Collection<Attachment> attachments) {
        final List<FileDescriptor> attachingFiles = new ArrayList<>();
        if (attachments != null && attachments.size() > 0) {
            for (Attachment attachment : attachments) {
                if (attachment.getAttachmentMethod() == AttachmentMethod.Embedded)
                    attachingFiles.add(attachment.getFile());
            }
        }
        return attachingFiles;
    }

    private String sendMassEmails(String caption, String body, List<FileDescriptor> attachingFiles, Collection<Client> clients, UUID mailingID) {
        final List<String> emails = new ArrayList<>();
        for (Client client: clients){
            if (!(client.getBadEmail()!= null && client.getBadEmail())){
                emails.add(client.getEmail());
                //log.info("email sent to user "+client.getEmail());
            }
        }
        final MailMessage message = new MailMessage()
                .withSubject(caption)
                .withBody(body)
                .withMessageId(String.valueOf(mailingID))
                .withAttachments(attachingFiles);
        try {
            massMailingService.send(message, emails);
        } catch (RemoteException remEx) {
            persistStatus(mailingID, OnetimeMailingStatus.Error);
            final Throwable cause = remEx.getCause();
            if (cause instanceof MassMailingServiceException) {
                final MassMailingServiceException ex = (MassMailingServiceException) cause;
                switch (ex.getErrorCode()) {
                    case CONNECT_TO_FTP_SERVER_ERROR:
                        return "Couldn't connect to ConsultantPlus Mailing Service FTP server";
                    case LOGIN_TO_FTP_SERVER_ERROR:
                        return "ConsultantPlus Mailing Service FTP server login error";
                    case UPLOAD_RECEIVERS_LIST_ERROR:
                        return "Couldn't upload receivers list to ConsultantPlus Mailing Service FTP server";
                    case UPLOAD_MAIL_MESSAGE_TEMPLATE_ERROR:
                        return "Couldn't upload email message template to ConsultantPlus Mailing Service FTP server";
                }
            }
            return "Error during work with ConsultantPlus Mailing Service FTP";
        }
        return null;
    }

    private List<OnetimeMailing> selectOnetimeMailings(OnetimeMailingStatus status) {
        List<OnetimeMailing> list;
        Date now = dts.getCurrentDateTime();
        Transaction tx = persistence.createTransaction();
        TypedQuery<OnetimeMailing> query;
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("Select om from gklients$OnetimeMailing om \n" +
                            "where  om.sendingDate <= ?1 and om.status = ?2"
                    , OnetimeMailing.class);
            query.setParameter(1, now);
            query.setParameter(2, status);
            query.setView(OnetimeMailing.class, "onetimeMailing-forSendOut");
            list = query.getResultList();
            tx.commit();
        } catch (NoResultException e) {
            return null;
        } finally {
            tx.end();
        }
        return list;
    }


    private String updateStatistics(List<OnetimeMailing> mailingsList) {
        StringBuilder result = new StringBuilder();

        List<MailingStatistics> statList = new ArrayList<>();
        for (OnetimeMailing mailing : mailingsList) {
            MailingStatistics statistics = mailing.getMailingStatistics();
            try {
                boolean updated = mailingStatisticService.refreshStatistics(statistics);
                if (updated) {
                    statList.add(statistics);
                    if ((statistics.getPlanned() > 0) && (statistics.getCompleted() + statistics.getFailed()) == statistics.getPlanned()) {
                        persistStatus(mailing.getId(), OnetimeMailingStatus.Completed);
                    }
                    result.append("Statistics for mailing ")
                            .append(mailing.getSubject())
                            .append("\"")
                            .append(" updated");
                }

            } catch (Exception e) {
                result.append("Exception while updating statistic for mailing \"")
                        .append(mailing.getSubject())
                        .append("\"");
            }
        }
        if (!statList.isEmpty()) {
            EntityWorker.persist(statList);
        }
        return result.toString();
    }

    private void persistStatus(UUID mailingId, OnetimeMailingStatus status) {
        OnetimeMailing mailing = (OnetimeMailing) EntityWorker.getEntity(OnetimeMailing.class, mailingId);
        if (mailing != null && mailing.getStatus() != status) {
            mailing.setStatus(status);
            EntityWorker.persist(mailing);
        }

    }


}
