/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.PersistenceTools;
import com.haulmont.cuba.core.listener.BeforeDeleteEntityListener;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.glavkniga.gklients.crud.AccessGranter;
import ru.glavkniga.gklients.crud.Gadd;
import ru.glavkniga.gklients.crud.Gdelete;
import ru.glavkniga.gklients.crud.Gedit;
import ru.glavkniga.gklients.crudentity.Regkey;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ElverSubscription;

import javax.inject.Inject;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author LysovIA
 */

// TODO https://github.com/cuba-platform/cuba/blob/8836668c2fd0ffa88f2e91b9df6f64565c28b983/modules/global/src/com/haulmont/cuba/core/sys/encryption/Md5EncryptionModule.java
// Для генерации хешей сделать из оригинала что то подобное юзабельное для всего приложения

@Component("gklients_ClientUpdateListener")
public class ClientUpdateListener implements BeforeUpdateEntityListener<Client>,
        BeforeDeleteEntityListener<Client>, BeforeInsertEntityListener<Client> {

    @Inject
    private PersistenceTools persistenceTools;

    @Override
    public void onBeforeInsert(Client client, EntityManager entityManager) {
        client.setEmailHash(passHashSet(client.getEmail()));
        client.setPasswordHash(passHashSet(client.getPassword()));
        Gadd gadd = new Gadd();
        gadd.setDuplicateIgnoreMode(false);
        gadd.addObject(client);
    }

    @Override
    public void onBeforeUpdate(Client client, EntityManager entityManager) {
        if (persistenceTools.isDirty(client, "email")) {
            client.setEmailHash(passHashSet(client.getEmail()));
            List<ElverSubscription> clientSubscriptions = AccessGranter.getElverSubscriptions(client);
            if (clientSubscriptions != null && clientSubscriptions.size() > 0) {
                clientSubscriptions.forEach(subscription -> {
                    Regkey regkey = new Regkey();
                    regkey.setEmail(client.getEmail());
                    Gedit gedit = new Gedit();
                    gedit.addFilterField("regkey", String.valueOf(subscription.getRegkey()));
                    gedit.editObject(regkey);
                });
            }
        }

        if (persistenceTools.isDirty(client, "password")) {
            client.setPasswordHash(passHashSet(client.getPassword()));
        }

        if (persistenceTools.isDirty(client, "id", "email", "password", "isBlocked")) {
            Gedit gedit = new Gedit();
            gedit.addFilterField("id", String.valueOf(client.getId()));
            gedit.editObject(client);
        }
    }

    @Override
    public void onBeforeDelete(Client client, EntityManager entityManager) {
        List<ElverSubscription> clientSubscriptions = AccessGranter.getElverSubscriptions(client);
        if (clientSubscriptions != null && clientSubscriptions.size() > 0) {
            clientSubscriptions.forEach(subscription -> {
                AccessGranter.recallAll(client, subscription);
            });
        }
        Gdelete gdelete = new Gdelete(Client.class);
        gdelete.deleteObject(client);
    }

    private String passHashSet(String valueToHash) {
        //TODO вынести соль в настройки, желательно в БД, чтобы можно было их менять на лету
        //TODO сделать экран для редактирования всех настроек, чтобы можно было админу проверять / корректировать их
        String static_salt = "xdngtsrhsdng";
        String hashedString = "";

        if (valueToHash != null) {
            valueToHash = static_salt + valueToHash;
            MessageDigest m;
            try {
                m = MessageDigest.getInstance("MD5");
                m.reset();
                m.update(valueToHash.getBytes());
                byte[] digest = m.digest();

                BigInteger bigInt = new BigInteger(1, digest);
                hashedString = bigInt.toString(16);
                while (hashedString.length() < 32) {
                    hashedString = "0" + hashedString;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return hashedString;
    }


}