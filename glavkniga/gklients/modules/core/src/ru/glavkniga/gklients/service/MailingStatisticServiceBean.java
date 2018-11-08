/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.FailedEmail;
import ru.glavkniga.gklients.entity.MailingStatistics;
import ru.glavkniga.gklients.entity.OnetimeMailing;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

/**
 * @author Хомяк
 */
@Service(MailingStatisticService.NAME)
public class MailingStatisticServiceBean implements MailingStatisticService {

    @Inject
    private MassMailingService mailingService;

    @Inject
    private Metadata metadata;

    @Inject
    private DateTimeService dts;

    private Logger log = LoggerFactory.getLogger(MailingStatisticServiceBean.class);


    @Override
    public boolean refreshStatistics(MailingStatistics statistics) {
        boolean updated = false;

        //assume full graph loaded with OnetimeMailing included based on mailingStatistics-browse view
        OnetimeMailing mailing = statistics.getOnetimeMailing();

        // TODO define logic of analysis of current result against existing data.
        // 1. All amount of planned emails must be found in statistic if Mailing scheduled date pass. If it's not pass then there's no reasons to check statistics again cause it contain no data.
        // 2. Completed emails amount must raise every refresh until Completed + Failed = Planned. If so, there's no reasons to check statistics cause all data already collected.
        // 3. Sending emails amount must decrease every refresh until 0. If so then no reason to check statistics again.
        // 4. Failed email amount can remain 0 or raise every refresh until Completed + Failed = Planned. If so then no reason to check statistics - all data already collected.
        // Assumption: if Sending remain same in 24h or more that means all sending emails failed. Check log parser for details.

        Integer planned = statistics.getPlanned() != null ? statistics.getPlanned() : 0;
        Integer completed = 0;
        Integer failed = 0;

        Set<Client> clientSet = mailing.getClient();
        Map<String, Client> emailsMappedClients = new HashMap<>();

        for (Client client : clientSet) {
            emailsMappedClients.put(client.getEmail(), client);
        }
        String mailingSubject = mailing.getSubject();
        List<FailedEmail> failedEmails = new ArrayList<>();
        List<FailedEmail> existingFailedEmails = collectFailedEmails(statistics);
        LocalDate readFrom = dts.makeLocalDate(mailing.getSendingDate());

        Map<MassMailingService.SentEmailInfo, Boolean> statMap = mailingService.readStatistics(readFrom);
        if (statMap != null && !statMap.isEmpty()) {
            for (Map.Entry<MassMailingService.SentEmailInfo, Boolean> e : statMap.entrySet()) {
                MassMailingService.SentEmailInfo emailInfo = e.getKey();
                if (mailingSubject.equals(emailInfo.getSubject())
                        && emailsMappedClients.containsKey(emailInfo.getEmail())) {
                /* result could be in 2 states:
                * * true - means email sending succeed
                * * false - means email sending ongoing or failed
                * this means we don't yet have proper status to determine sending failed
                * */
                    if (e.getValue()) {
                        completed++;
                    } else {
                        failed++;
                        //assume failed email goes here
                        if (!ifEmailInList(existingFailedEmails, emailInfo.getEmail())) {
                            // Create new FailedEmail
                            Client client = emailsMappedClients.get(emailInfo.getEmail());
                            FailedEmail failedEmail = metadata.create(FailedEmail.class);
                            failedEmail.setClient(client);
                            failedEmail.setMailingStatistics(statistics);
                            failedEmail.setOnetimeMailing(mailing);
                            failedEmail.setReason("Log Parser reported fail");
                            failedEmails.add(failedEmail);
                        }
                    }
                }
            }
        }

//        if (!planned.equals(count)) {
//            log.warn("Statistics for mailing " + mailing.getId() + " incomplete. Only " + count + " items of " + planned + " found");
//        }

        if (!statistics.getCompleted().equals(completed)) {
            updated = true;
            statistics.setCompleted(completed);
        }

        if (!statistics.getFailed().equals(failed)) {
            updated = true;
            statistics.setFailed(failed);
        }
        Integer sending = planned - completed - failed;

        if(!statistics.getSending().equals(sending)){
            updated = true;
            statistics.setSending(sending);
        }

        if (!failedEmails.isEmpty()) {
            this.persist(failedEmails);
        }

        return updated;
    }


    private void persist(Collection<? extends Entity> entities) {
        Persistence persistence = AppBeans.get(Persistence.NAME);
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            entities.forEach(em::persist);
            tx.commit();
        } finally {
            tx.end();
        }
    }

    private static List<FailedEmail> collectFailedEmails(MailingStatistics statistics) {

        List<FailedEmail> emailsSet;
        Persistence persistence = AppBeans.get(Persistence.NAME);
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            TypedQuery<FailedEmail> query = em.createQuery("SELECT e " +
                    "from gklients$FailedEmail e " +
                    "WHERE e.mailingStatistics.id = ?1", FailedEmail.class);
            query.setParameter(1, statistics.getId());
            query.setViewName("failedEmails-withClient");
            emailsSet = query.getResultList();
            tx.commit();
        } finally {
            tx.end();
        }
        return emailsSet;
    }

    private static boolean ifEmailInList(List<FailedEmail> existingEmails, String email) {
        if (existingEmails == null || existingEmails.isEmpty()) {
            return false;
        }
        for (FailedEmail existingFailedEmail : existingEmails) {
            try {
                String existingEmail = existingFailedEmail.getClient().getEmail();
                if (existingEmail.equals(email))
                    return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

}