/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients;


/**
 * Created by vdergachev on 26.05.17.
 */

import com.haulmont.cuba.core.entity.Entity;
import org.junit.ClassRule;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import java.nio.file.Paths;

import static com.google.common.base.Strings.isNullOrEmpty;

public class BaseIT {

    @ClassRule
    public static AppTestContainer container = AppTestContainer.create();

    protected static void cleanUpDB() {
        container.persistence().createTransaction().execute(em -> {
            // TODO SWitch to TRANCATE TABLE XXX CASCADE
            em.createNativeQuery("TRUNCATE TABLE gklients_client_service CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE gklients_client CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE gklients_token CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE gklients_riz CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE gklients_magazine_issue CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE gklients_schedule CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE gklients_tarif CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE gklients_tarif CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE gklients_elver_subscription CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE gklients_client_distribution_settings CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE gklients_gk_news CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE GKLIENTS_GK_NEWS_IMAGE CASCADE").executeUpdate();
        });
    }

    protected static <T extends Entity> T save(final T entity) {
        return container.persistence().createTransaction().execute(em -> {
            em.persist(entity);
            return entity;
        });
    }

    protected static <T extends Entity> T update(final T entity) {
        return container.persistence().createTransaction().execute(em -> {
            em.merge(entity);
            return entity;
        });
    }

    // TODO Remove, now it useless
    public static String getPathInTempFolder(final String... folders) {
        if (folders.length == 0) {
            throw new IllegalArgumentException("No folders passed to the method");
        }
        final String tmpPath = System.getProperty("java.io.tmpdir");
        if (isNullOrEmpty(tmpPath)) {
            throw new IllegalStateException("Can't get temporary folder path");
        }
        return Paths.get(Paths.get(tmpPath).toString(), folders).toString();
    }

    protected <T> T getTargetObject(final Object proxy) throws Exception {
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return (T) ((Advised) proxy).getTargetSource().getTarget();
        } else {
            return (T) proxy;
        }
    }
}
