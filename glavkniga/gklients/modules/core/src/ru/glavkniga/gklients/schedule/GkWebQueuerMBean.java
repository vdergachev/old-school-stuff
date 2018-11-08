/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.schedule;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.entity.Entity;
import org.springframework.stereotype.Component;
import ru.glavkniga.gklients.crud.Gadd;
import ru.glavkniga.gklients.crud.Gdelete;
import ru.glavkniga.gklients.crud.Gedit;
import ru.glavkniga.gklients.crud.Gget;
import ru.glavkniga.gklients.entity.WebsiteQueueFilter;
import ru.glavkniga.gklients.enumeration.WebstieOperations;
import ru.glavkniga.gklients.entity.WebstieQueue;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by LysovIA on 28.02.2017.
 */

@Component(GkWebQueuer.NAME)
public class GkWebQueuerMBean implements GkWebQueuer {
    @Inject
    private Persistence persistence;

    private String result = "";

    @Override
    public String processQueue() {

        List<WebstieQueue> wQList = this.loadQueue();
        if (wQList != null && wQList.size() > 0) {
            wQList.forEach(webstieQueue -> {
                String localResult = "";
                UUID objectId = webstieQueue.getEntityId();
                String entityName = webstieQueue.getEntityName();
                WebstieOperations operation = webstieQueue.getOperation();
                try {
                    Class entityClass = Class.forName(entityName);
                    Entity object = this.loadObject(entityClass, objectId);
                    Set<WebsiteQueueFilter> webstieQueueFilter = webstieQueue.getWebsiteQuieueFilter();
                    switch (operation) {
                        case Get:
                            localResult = "GET operation found. ";
                            Gget gget = new Gget(entityClass);

                            for (WebsiteQueueFilter filterItem : webstieQueueFilter) {
                                gget.addFilterProperty(filterItem.getParamName(), filterItem.getValue());
                            }
                            Map<String, Object> entityMap = gget.getObjects();
                            if (entityMap != null && entityMap.size() > 0) {
                                entityMap.forEach((str, obj) -> {
                                    this.saveObject((Entity) obj);
                                });
                                this.result += "Got " + entityMap.size() + " objects. ";
                            }
                            break;
                        case Add:
                            Gadd gadd = new Gadd();
                            gadd.addObject(object);
                            localResult = "Add operation found. 1 object added. ";
                            break;
                        case Edit:
                            Gedit gedit = new Gedit();
                            for (WebsiteQueueFilter filterItem : webstieQueueFilter) {
                                gedit.addFilterField(filterItem.getParamName(), filterItem.getValue());
                            }
                            gedit.editObject(object);
                            localResult = "Edit operation found. 1 object edited. ";
                            break;
                        case Delete:
                            Gdelete gdelete = new Gdelete(entityClass);
                            for (WebsiteQueueFilter filterItem : webstieQueueFilter) {
                                gdelete.addFilterField(filterItem.getParamName(), filterItem.getValue());
                            }
                            gdelete.deleteObject(object);
                            localResult = "Delete operation found. 1 object deleted. ";
                            break;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                this.removeQueryItem(webstieQueue.getId());
                this.result += localResult;
            });
        }

        return this.result.isEmpty() ? this.result : "No query items found";
    }

    private List<WebstieQueue> loadQueue() {
        List<WebstieQueue> wQList = null;
        TypedQuery<WebstieQueue> query;
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("Select wq from gklients$WebsiteQuery wq LIMIT 10", WebstieQueue.class);
            query.setView(WebstieQueue.class, "websiteQueueFilters");
            wQList = query.getResultList();
            tx.commit();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.end();
        }
        return wQList;
    }

    private void removeQueryItem(UUID itemId) {
        TypedQuery<WebstieQueue> query;
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("DELETE from gklients$WebsiteQuery where id=?1", WebstieQueue.class);
            query.setParameter(1, itemId);
            query.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.end();
        }
    }

    private Entity loadObject(Class theclass, UUID uuid) {
        Entity entity = null;
        final Transaction tx = persistence.createTransaction();
        try {
            final EntityManager em = persistence.getEntityManager();
            entity = (Entity) em.find(theclass, uuid);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.end();
        }
        return entity;
    }

    private void saveObject(Entity object) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            em.persist(object);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.end();
        }
    }
}
