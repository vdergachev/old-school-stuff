/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.app.FileStorageService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.glavkniga.gklients.entity.MunObraz;
import ru.glavkniga.gklients.enumeration.Region;
import ru.glavkniga.gklients.entity.Riz;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author LysovIA
 */
@Service(CsvParserService.NAME)
public class CsvParserServiceBean implements CsvParserService {


    @Inject
    private FileStorageService fileStorageService;
    @Inject
    Persistence persistence;
    @Inject
    private Metadata metadata;

    private Logger log = LoggerFactory.getLogger(CsvParserServiceBean.class);


    public void parseCsvToMunObraz(FileDescriptor file) {
        String cvsSplitBy = ";";
        try {

            byte[] bytes = null;
            try {
                bytes = fileStorageService.loadFile(file);
            } catch (FileStorageException e) {
                e.printStackTrace();
            }
            String[] string_arr = new String(bytes, "UTF-8").split("\r\n");

            Transaction tx = persistence.createTransaction();
            try {

                EntityManager entityManager = persistence.getEntityManager();
                for (String line : string_arr) {
                    String[] record = line.split(cvsSplitBy);
                    if (record.length > 1) {
                        MunObraz munObraz = metadata.create(MunObraz.class);
                        munObraz.setOktmo(record[0]);
                        munObraz.setTitle(record[1]);
                        munObraz.setCapital(record[2]);
                        munObraz.setOsmId(record[3]);
                        munObraz.setRegion(Region.fromId(record[4]));
                        entityManager.persist(munObraz);
                    }
                }
                tx.commit();
            } finally {
                tx.end();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }


    public void parseCsvToRiz(FileDescriptor file) {
        String cvsSplitBy = ";";
        try {

            byte[] bytes = null;
            try {
                bytes = fileStorageService.loadFile(file);
            } catch (FileStorageException e) {
                e.printStackTrace();
            }
            String[] string_arr = new String(bytes, "UTF-8").split("\r\n");
            for (String line : string_arr) {
                Transaction tx = persistence.createTransaction();
                try {
                    EntityManager entityManager = persistence.getEntityManager();
                    String[] record = line.split(cvsSplitBy);
                    try {
                        if (record.length > 1) {

                            Riz riz = metadata.create(Riz.class);
                            if (record[0].length() > 0) {
                                int number = Integer.valueOf(record[0].trim());
                                riz.setNumber(number);
                            }
                            riz.setName(record[1].trim());
                            riz.setCity(record[2].trim());
                            riz.setRegion(Region.fromId(record[4].trim()));
                            riz.setPhone(record[6].trim());
                            riz.setElverEmail(record[7].trim());
                            riz.setDeliveryAddress(record[8].trim());
                            riz.setDeliveryEmail(record[9].trim());
                            riz.setDeliveryCompany(record[10].trim());
                            riz.setOrderPerson(record[11].trim());
                            riz.setOrderPhone(record[12].trim());
                            riz.setOrderEmail(record[13].trim());
                            riz.setOrderAccountist(record[14].trim());
                            riz.setPrPerson(record[15].trim());
                            riz.setPrPhone(record[16].trim());
                            riz.setPrEmail(record[17].trim());
                            riz.setDistrPerson(record[18].trim());
                            riz.setDistrEmail(record[19].trim());
                            riz.setWsName(record[20].trim());
                            riz.setWsAddress(record[21].trim());
                            riz.setWsPhone(record[22].trim());
                            riz.setWsPerson(record[23].trim());
                            riz.setWsEmail(record[24].trim());
                            riz.setWsWebsite(record[25].trim());
                            riz.setWsTerritory(record[26].trim());

                            if (record[3].length() > 0) {
                                int podhostNumber = Integer.valueOf(record[3]);
                                List<Riz> list = getRizs(podhostNumber);
                                if (list.size() > 0) {
                                    riz.setPodhost(list.iterator().next());
                                }
                            }

                            Set<MunObraz> setMo = getMunObrazs(record[27]);
                            if (setMo.size() > 0) {
                                riz.setMunObraz(setMo);
                            }
                            entityManager.persist(riz);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        log.debug("Index out of bounds for the entry:" + record[0] + ": " + record[1]);
                        e.printStackTrace();
                    } catch (NumberFormatException e) {
                        log.debug("Format of index is incorrect for entry" + record[0] + ": " + record[1]);
                        e.printStackTrace();
                    }
                    tx.commit();
                } finally {
                    tx.end();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private List<Riz> getRizs(int value) {
        List<Riz> list;
        Transaction tSelectRiz = persistence.createTransaction();
        try {
            EntityManager selectManager = persistence.getEntityManager();
            TypedQuery<Riz> query = selectManager.createQuery(
                    "select o from gklients$Riz o where o.number = ?1 and o.podhost IS NULL" , Riz.class);
            query.setParameter(1, value);
            list = query.getResultList();
            tSelectRiz.commit();
        } finally {
            tSelectRiz.end();
        }
        return list;
    }

    private Set<MunObraz> getMunObrazs(String s) {
        String munObrazIds = s.trim();
        String[] munOktmoList = munObrazIds.split(",");
        Set<MunObraz> setMo = new HashSet<>();
        for (String munOktmo : munOktmoList) {
            List<MunObraz> listMO;
            Transaction tSelectMun = persistence.createTransaction();
            try {
                EntityManager selectManager = persistence.getEntityManager();
                TypedQuery<MunObraz> query = selectManager.createQuery(
                        "select o from gklients$MunObraz o where o.oktmo = ?1", MunObraz.class);
                query.setParameter(1, munOktmo.trim());
                listMO = query.getResultList();
                tSelectMun.commit();
            } finally {
                tSelectMun.end();
            }
            if (listMO.size() > 0) {
                for (MunObraz mo : listMO) {
                    setMo.add(mo);
                }
            }
        }
        return setMo;
    }
}