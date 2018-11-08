/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.schedule;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class EntityWorker {
    private static DataManager dataManager = AppBeans.get(DataManager.class);
    private static Metadata metadata = AppBeans.get(Metadata.class);
    private static Logger log = LoggerFactory.getLogger(EntityWorker.class);

    public static void persist(Collection<? extends Entity> entities) {
        //DataManager dataManager = AppBeans.get(DataManager.class);
        CommitContext commitContext = new CommitContext(entities);
        dataManager.commit(commitContext);
        log.info(String.valueOf(entities.size()) + " items of type " + entities.iterator().next().getClass().getName() + " persisted");
    }
    //TODO worker can't persist new items with nested cascade entities. fix it
    public static void persist(Entity entity) {
        CommitContext commitContext = new CommitContext(entity);
        dataManager.commit(commitContext);
        log.info("1 items of type " + entity.getClass().getName() + " persisted");
    }

    public static Entity getEntity(Class<? extends Entity> clazz, UUID uuid, String viewName) {
        LoadContext<? extends Entity> loadContext = LoadContext.create(clazz)
                .setId(uuid).setView(viewName);
        Entity entity = dataManager.load(loadContext);
        if (entity == null) {
            entity = metadata.create(clazz);
        }
        return entity;
    }

    public static Entity getEntity(Class<? extends Entity> clazz, UUID uuid) {
        return getEntity(clazz, uuid, "_local");
    }


    public static Entity getEntityByFieldValue(Class<? extends Entity> clazz, String field, Object value) {
        return getEntityByFieldValue(clazz, field, value, "_local");
    }

    public static Entity getEntityByFieldValue(Class<? extends Entity> clazz, String field, Object value, String viewName) {
        List<? extends Entity> resultList = getEntityListByFieldValue(clazz, field, value, viewName);
        if (resultList != null && resultList.size()>0) {
            return resultList.get(0);
        } else
            return null;
    }

    public static List<? extends Entity> getEntityListByFieldValue(Class<? extends Entity> clazz, String field, Object value, String viewName) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("Select s from gklients$")
                .append(clazz.getSimpleName())
                .append(" s where s.")
                .append(field)
                .append(" = :value ")
                .append("ORDER BY s.createTs DESC");

        LoadContext.Query query = new LoadContext.Query(queryString.toString());
        query.setParameter("value", value);
        LoadContext<? extends Entity> loadContext = LoadContext.create(clazz)
                .setQuery(query)
                .setView(viewName);
        return dataManager.loadList(loadContext);
    }


}
