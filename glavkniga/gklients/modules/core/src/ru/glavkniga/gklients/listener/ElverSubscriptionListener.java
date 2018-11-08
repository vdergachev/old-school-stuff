/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.PersistenceTools;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.listener.BeforeDeleteEntityListener;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import org.springframework.stereotype.Component;
import ru.glavkniga.gklients.crud.AccessGranter;
import ru.glavkniga.gklients.crud.Gadd;
import ru.glavkniga.gklients.crudentity.Regkey;
import ru.glavkniga.gklients.entity.*;
import ru.glavkniga.gklients.service.DefineIssuesRangeService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author IgorLysov
 */
@Component("gklients_ElverSubscriptionListener")
public class ElverSubscriptionListener
        implements
        BeforeInsertEntityListener<ElverSubscription>,
        BeforeUpdateEntityListener<ElverSubscription>,
        BeforeDeleteEntityListener<ElverSubscription>{

    @Inject
    private Persistence persistence;

    @Override
    public void onBeforeInsert(ElverSubscription entity, EntityManager entityManager) {
        Metadata metadata = AppBeans.get(Metadata.NAME);
        ClientService cs = metadata.create(ClientService.class);
        cs.setClient(entity.getClient());
        cs.setService("1");
        cs.setActivationDate(entity.getRequestDate());
        entityManager.persist(cs);
        this.sendRegKey(entity, entity.getClient());
        this.grantAccessInSeparateThread(entity);
    }

    @Override
    public void onBeforeUpdate(ElverSubscription entity, EntityManager entityManager) { //TODO needs refactoring
        Client client = entity.getClient();
        ElverSubscription oldEntity = null;
        Client oldClient = null;
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            oldEntity = em.find(ElverSubscription.class, entity.getId(), "elverSubscription-browse");
            oldClient = oldEntity.getClient();
        } finally {
            tx.end();
        }
        if (oldEntity != null) {
            if (!client.getId().equals(oldClient.getId())) { //Client changed - only recall access from old entity and grand for new one
                recalAccessInSeparateThread(oldEntity);
                grantAccessInSeparateThread(entity);
                sendRegKey(entity, client);
            } else {

                DefineIssuesRangeService issueService = AppBeans.get(DefineIssuesRangeService.class);
                MagazineIssue oldIssueStart = oldEntity.getIssueStart();
                MagazineIssue oldIssueEnd = oldEntity.getIssueEnd();
                List<MagazineIssue> listToDelete = new ArrayList<>();

                if (!oldEntity.getTarif().equals(entity.getTarif())) { //Tariff changed
                    sendRegKey(entity, client);
                }

                if (!entity.getIssueStart().equals(oldIssueStart)) { //Issue start changed;
                    listToDelete.addAll(issueService.getMagazineIssuesInBetween(oldIssueStart, entity.getIssueStart()));
                }
                if (!entity.getIssueEnd().equals(oldIssueEnd)) {//Issue end changed;
                    listToDelete.addAll(issueService.getMagazineIssuesInBetween(entity.getIssueEnd(), oldIssueEnd));
                }
                if (listToDelete.size() > 0) {
                    MagazineIssue issueFirst = listToDelete.get(0);
                    MagazineIssue issueLast = listToDelete.get(listToDelete.size() - 1);
                    if (issueFirst.getId().equals(entity.getIssueStart().getId())
                            || issueFirst.getId().equals(entity.getIssueEnd().getId())) {
                        listToDelete.remove(issueFirst);
                    }

                    if (issueLast.getId().equals(entity.getIssueStart().getId())
                            || issueLast.getId().equals(entity.getIssueEnd().getId())) {
                        listToDelete.remove(issueLast);
                    }
                    recalAccessInSeparateThread(client, listToDelete);
                }
                grantAccessInSeparateThread(entity);
            }
        }
    }

    @Override
    public void onBeforeDelete(ElverSubscription entity, EntityManager entityManager) {
        AccessGranter.recallAll(entity.getClient(), entity);
    }

    private void grantAccessInSeparateThread(final ElverSubscription entity) {
        final ExecutorService service = Executors.newFixedThreadPool(1);
        final SecurityContext securityContext = AppContext.getSecurityContext();
        service.submit(() -> {
            AppContext.setSecurityContext(securityContext);

            Client client;
            Transaction tx = persistence.createTransaction();
            try {
                EntityManager em = persistence.getEntityManager();
                client = em.find(Client.class, entity.getClient().getId(), "client-view");
                tx.commit();
            } finally {
                tx.end();
            }
            if (!AccessGranter.isClient(client.getId())) {
                AccessGranter.addClient(client);
            }
            AccessGranter.grantAll(client, entity);
            AccessGranter.toggleSync(true);
        });
        service.shutdown();
    }

    private void sendRegKey(ElverSubscription entity, Client client) {
        Metadata metadata = AppBeans.get(Metadata.NAME);
        /*
        Tarif tarif = metadata.create(Tarif.class);
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            tarif = em.find(Tarif.class, entity.getTarif().getId(), "_minimal");
            tx.commit();
        } finally {
            tx.end();
        }
        */
//TODO verify if this code works on prod

        final Tarif tarif = entity.getTarif();
        final PersistenceTools tools = persistence.getTools();
        if(!tools.isLoaded(tarif, "tarifNumber")){
            persistence.getEntityManager().reload(tarif, "_minimal");
        }

        Regkey regkey = metadata.create(Regkey.class);
        regkey.setEmail(client.getEmail());
        regkey.setRegkey(entity.getRegkey());
        if (tarif != null) {
            regkey.setTarif(String.valueOf(tarif.getTarifNumber()));
        }
        if (entity.getActivation_date() != null)
            regkey.setDate_activation(entity.getActivation_date());
        Gadd gadd = new Gadd();
        gadd.setDuplicateIgnoreMode(false);
        gadd.addObject(regkey);
    }

    private void recalAccessInSeparateThread(final Client client, final List<MagazineIssue> issues) {
        final ExecutorService service = Executors.newFixedThreadPool(1);
        final SecurityContext securityContext = AppContext.getSecurityContext();
        service.submit(() -> {
            AppContext.setSecurityContext(securityContext);
            issues.forEach(issue -> {
                AccessGranter.recallAccess(client, issue);
            });

        });
        service.shutdown();


    }

    private void recalAccessInSeparateThread(final ElverSubscription entity) {
        final ExecutorService service = Executors.newFixedThreadPool(1);
        final SecurityContext securityContext = AppContext.getSecurityContext();
        final DefineIssuesRangeService issueService = AppBeans.get(DefineIssuesRangeService.class);
        service.submit(() -> {
            AppContext.setSecurityContext(securityContext);
            Client client = entity.getClient();
            List<MagazineIssue> issues = issueService.getMagazineIssuesForES(entity);
            issues.forEach(issue -> {
                AccessGranter.recallAccess(client, issue);
            });

        });
        service.shutdown();

    }

}