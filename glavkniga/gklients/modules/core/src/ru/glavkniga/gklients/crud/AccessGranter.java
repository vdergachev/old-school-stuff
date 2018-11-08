/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crud;

import com.beust.jcommander.internal.Nullable;
import com.haulmont.cuba.core.*;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.glavkniga.gklients.crudentity.ElverAccess;
import ru.glavkniga.gklients.crudentity.SeminarAccess;
import ru.glavkniga.gklients.crudentity.SituationAccess;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientService;
import ru.glavkniga.gklients.entity.ElverSubscription;
import ru.glavkniga.gklients.entity.MagazineIssue;
import ru.glavkniga.gklients.service.DefineIssuesRangeService;

import javax.persistence.NoResultException;
import java.util.*;

/**
 * Created by LysovIA on 27.02.2017.
 */
public class AccessGranter {

    private static boolean elverAccessGranted = false;
    private static boolean seminarAccessGranted = false;

    private static Logger log = LoggerFactory.getLogger(AccessGranter.class);


    public static void grantElverAccess(Client client, MagazineIssue magazineIssue, Date dateFrom, Date dateTo) {
        Metadata metadata = AppBeans.get(Metadata.class);
        ElverAccess ea = metadata.create(ElverAccess.class);
        ea.setClient(client);
        ea.setNumber(magazineIssue.getNumber());
        ea.setYear(magazineIssue.getYear());
        ea.setBeginDate(dateFrom);
        ea.setEndDate(dateTo);

        Gadd elverAccessAdder = new Gadd();
        elverAccessAdder.setDuplicateIgnoreMode(false);
        elverAccessAdder.addObject(ea);
    }


    private static void grantSeminarAccess(Client client, MagazineIssue magazineIssue, Date dateFrom, Date dateTo) {
        Metadata metadata = AppBeans.get(Metadata.class);
        SeminarAccess sa = metadata.create(SeminarAccess.class);
        sa.setClient(client);
        sa.setNumber(magazineIssue.getNumber());
        sa.setYear(magazineIssue.getYear());
        sa.setBeginDate(dateFrom);
        sa.setEndDate(dateTo);

        Gadd elverAccessAdder = new Gadd();
        elverAccessAdder.setDuplicateIgnoreMode(false);
        elverAccessAdder.addObject(sa);
    }

    public static void recallAccess(Client client, MagazineIssue magazineIssue) {
        int magazineID = magazineIssue.getMagazine().getMagazineID(); //TODO catch NPE here
        switch (magazineID) {
            case 1:
                recallElverAccess(client, magazineIssue);
                break;
            case 2:
                recallSeminarAccess(client, magazineIssue);
                break;
        }
    }

    private static void recallElverAccess(Client client, MagazineIssue magazineIssue) {
        Metadata metadata = AppBeans.get(Metadata.class);
        ElverAccess ea = metadata.create(ElverAccess.class);
        ea.setNumber(magazineIssue.getNumber());
        ea.setYear(magazineIssue.getYear());
        ea.setClient(client);
        Gdelete gdelete = new Gdelete(ElverAccess.class);
        gdelete.deleteObject(ea);
    }

    private static void recallSeminarAccess(Client client, MagazineIssue magazineIssue) {
        Metadata metadata = AppBeans.get(Metadata.class);
        SeminarAccess sa = metadata.create(SeminarAccess.class);
        sa.setNumber(magazineIssue.getNumber());
        sa.setYear(magazineIssue.getYear());
        sa.setClient(client);
        Gdelete gdelete = new Gdelete(SeminarAccess.class);
        gdelete.deleteObject(sa);
    }

    public static void grantSeminarAccess(Client client, ElverSubscription elverSubscription) {
        grantAccess(client, elverSubscription, "2");
    }

    public static void grantElverAccess(Client client, ElverSubscription elverSubscription) {
        grantAccess(client, elverSubscription, "1");
    }


    private static void grantAccess(Client client, ElverSubscription elverSubscription, @Nullable String serviceNumber) {
        DefineIssuesRangeService issueService = AppBeans.get(DefineIssuesRangeService.class);
        List<MagazineIssue> issues = issueService.getMagazineIssuesForES(elverSubscription);
        Date dateFrom = issueService.getDateFrom(elverSubscription);
        Date dateTo = issueService.getDateTo(elverSubscription);

        //adding two weeks for end Date based on requirement from 17.05.2017 ESobko
        int noOfDays = 14; //i.e two weeks
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTo);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
        Date dateToPlusTwoWeeks = calendar.getTime();

        if ((issues != null) && issues.size() > 0) {
            issues.forEach((issue) -> {
                int magazineID = issue.getMagazine().getMagazineID();
                switch (magazineID) {
                    case 1:
                        if (serviceNumber == null || serviceNumber.equals("1")) {
                            grantElverAccess(client, issue, dateFrom, dateToPlusTwoWeeks);
                            elverAccessGranted = true;
                        }
                        break;
                    case 2:
                        if (serviceNumber == null || serviceNumber.equals("2")) {
                            grantSeminarAccess(client, issue, dateFrom, dateToPlusTwoWeeks);
                            seminarAccessGranted = true;
                        }
                        break;
                }
            });
            if (elverAccessGranted) addClientService(client, "1", elverSubscription.getActivation_date());
            if (seminarAccessGranted) addClientService(client, "2", elverSubscription.getActivation_date());
        }
    }

    public static void addClientService(Client client, String service, @Nullable Date dateActivation) {
        Metadata metadata = AppBeans.get(Metadata.class);
        ClientService clientService = metadata.create(ClientService.class);
        clientService.setClient(client);
        clientService.setService(service);
        if (dateActivation != null) {
            clientService.setActivationDate(dateActivation);
        }
        Gadd clientServiceAdder = new Gadd();
        clientServiceAdder.setDuplicateIgnoreMode(true);
        clientServiceAdder.addObject(clientService);
    }

    public static void grantSituationAccess(Client client, Date dateFrom, Date dateTo) {
        Metadata metadata = AppBeans.get(Metadata.class);
        SituationAccess sa = metadata.create(SituationAccess.class);
        sa.setClient(client);
        sa.setBeginDate(dateFrom);
        sa.setEndDate(dateTo);

        Gadd elverAccessAdder = new Gadd();
        elverAccessAdder.setDuplicateIgnoreMode(false);
        elverAccessAdder.addObject(sa);

        addClientService(client, "3", null);
    }

    public static void grantAll(Client client, ElverSubscription elverSubscription) {

        DefineIssuesRangeService issueService = AppBeans.get(DefineIssuesRangeService.class);
        grantAccess(client, elverSubscription, null);


        Date dateFrom = issueService.getDateFrom(elverSubscription);
        Date dateTo = issueService.getDateTo(elverSubscription);

        //adding two weeks for end Date based on requirement from 17.05.2017 ESobko
        int noOfDays = 14; //i.e two weeks
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTo);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
        Date dateToPlusTwoWeeks = calendar.getTime();

        grantSituationAccess(client, dateFrom, dateToPlusTwoWeeks);
    }

    public static void recallAll(Client client, ElverSubscription elverSubscription) {

        DefineIssuesRangeService issueService = AppBeans.get(DefineIssuesRangeService.class);
        List<MagazineIssue> issues = issueService.getMagazineIssuesForES(elverSubscription);

        issues.forEach(magazineIssue ->recallAccess(client, magazineIssue));
    }

    public static List<ElverSubscription> getElverSubscriptions(Client client) {
        Persistence persistence = AppBeans.get(Persistence.class);
        List<ElverSubscription> esList = new ArrayList<>();
        TypedQuery<ElverSubscription> esQuery;
        Transaction tx2 = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            esQuery = em.createQuery(
                    "SELECT t FROM gklients$ElverSubscription t WHERE t.deleteTs is null AND t.client.id=?1",
                    ElverSubscription.class);
            esQuery.setView(ElverSubscription.class, "elverSubscription-full");
            esQuery.setParameter(1, client);
            esList = esQuery.getResultList();
            tx2.commit();
        } catch (NoResultException e) {
            e.printStackTrace();
        } finally {
            tx2.end();
        }
        return esList;
    }

    public static void addClient(Client client) {
        Gadd clientAdder = new Gadd();
        clientAdder.addObject(client);
    }

    public static boolean isClient(UUID uuid) {
        Gget gget = new Gget(Client.class);
        List<UUID> websiteUserIds = gget.getIdList("id");
        if (websiteUserIds != null && websiteUserIds.size() > 0) {
            return websiteUserIds.contains(uuid);
        } else
            return false;
    }

    public static void toggleSync(boolean is_active) {
        Persistence persistence = AppBeans.get(Persistence.class);
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery("UPDATE sys_scheduled_task SET is_active = #active WHERE bean_name = #beanName");
            query.setParameter("active", is_active);
            query.setParameter("beanName", "gklients_GKSync");
            query.executeUpdate();
            tx.commit();
        } catch (NoResultException e) {
            e.printStackTrace();
        } finally {
            tx.end();
        }
    }

    private ElverSubscription getNextElverSubscription(ElverSubscription es){
        //TODO select from database next number
        return es;
    }

}
