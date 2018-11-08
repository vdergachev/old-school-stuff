/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.schedule;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.TimeSource;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.MagazineIssue;
import ru.glavkniga.gklients.entity.Schedule;
import ru.glavkniga.gklients.service.EmailerService;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.*;

/**
 * Created by LysovIA on 28.12.2015.
 */
@Component(IssueNotifier.NAME)
public class IssueNotifierMBean implements IssueNotifier {

    @Inject
    private Persistence persistence;

    @Inject
    private TimeSource timeSource;


    private Logger log = LoggerFactory.getLogger(getClass());

    public IssueNotifierMBean() {
        this.log = LoggerFactory.getLogger(getClass());
    }

    @Inject
    private EmailerService emailerService;

    public String sendNotifications() {
        Set<MagazineIssue> publishedIssues = loadPublishedIssue();
        if (publishedIssues == null) {
            return "No issue is published today";
        }
        int i = 0;

        for (MagazineIssue item : publishedIssues) {
            List<Client> clients = loadClients();
            if (clients != null && clients.size() > 0) {
                i += clients.size();
                for (Client client : clients) {
                    emailerService.notifyNewIssue(client, item);
                }
            }
        }
        return i + " emails are set to send out.";
    }

    private Set<MagazineIssue> loadPublishedIssue() {
        List<Schedule> schedules;
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            /*TypedQuery<Schedule>  query = em.createQuery("Select s FROM gklients$Schedule s\n" +
                    "where s.siteUpload between ?1 and ?2", Schedule.class);
*/
            TypedQuery<Schedule> query = em.createQuery("Select s FROM gklients$Schedule s " +
                            "join gklients$MagazineIssue mi on s.magazineIssue.id = mi.id " +
                            "join gklients$Magazine m on mi.magazine.id = m.id " +
                            "where " +
                            "m.magazineID in (1, 2) and " +
                            "s.siteUpload between ?1 and ?2 "
                    , Schedule.class);
            query.setParameter(1, getStartOfDay(timeSource.currentTimestamp()));
            query.setParameter(2, getEndOfDay(timeSource.currentTimestamp()));
            query.setView(Schedule.class, "schedule-service");
            schedules = query.getResultList();
            tx.commit();
        } catch (NoResultException e) {
            return null;
        } finally {
            tx.end();
        }
        if (schedules.size() == 0) {
            return null;
        }
        Set<MagazineIssue> magazineIssues = new HashSet<>();
        for (Schedule schedule : schedules) {
            magazineIssues.add(schedule.getMagazineIssue());
        }
        return magazineIssues;
    }

    private List<Client> loadClients() {

        List<Client> clientList;
        TypedQuery<Client> query;
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("select \n" +
                    "c\n" +
                    "from \n" +
                    "gklients$ElverSubscription es\n" +
                    "join gklients$Client c on es.client.id = c.id \n" +
                    "\n" +
                    "join gklients$Schedule ss on ss.magazineIssue.id = es.issueStart.id\n" +
                    "join gklients$Schedule se on se.magazineIssue.id = es.issueEnd.id \n" +
                    "\n" +
                    "where \n" +
                    "(ss.siteUpload <= ?1 and se.siteUpload >= ?1)\n" +
                    "and \n" +
                    "c.deleteTs is null\n" +
                    "and \n" +
                    "es.activation_date IS NOT null", Client.class);

            query.setParameter(1, timeSource.currentTimestamp());
            clientList = query.getResultList();
            tx.commit();
        } catch (NoResultException e) {
            return null;
        } finally {
            tx.end();
        }
        if (clientList.size() == 0) {
            return null;
        }

        return clientList;
    }

    private static Date getEndOfDay(Date date) {
        return DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.DATE), -1);
    }

    private static Date getStartOfDay(Date date) {
        return DateUtils.truncate(date, Calendar.DATE);
    }
}
