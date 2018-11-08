/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crud;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import ru.glavkniga.gklients.entity.WebsiteQueueFilter;
import ru.glavkniga.gklients.entity.WebstieQueue;

/**
 * Created by LysovIA on 01.03.2017.
 */
public class QueueWorker {

    public static  void addItemToQueue(WebstieQueue webstieQueue) {
        Persistence persistence = AppBeans.get(Persistence.class);
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            em.persist(webstieQueue);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.end();
        }
    }

    public static WebstieQueue createQueueItem() {
        Metadata metadata = AppBeans.get(Metadata.class);
        return metadata.create(WebstieQueue.class);
    }

    public static WebsiteQueueFilter createFilterItem() {
        Metadata metadata = AppBeans.get(Metadata.class);
        return metadata.create(WebsiteQueueFilter.class);
    }

}
