/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.service;

import com.haulmont.reports.entity.Report;
import ru.glavkniga.gklients.entity.Client;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author LysovIA
 */
public interface GKExchangeService {
    String NAME = "gklients_GKExchangeService";


    int countUsers();

    int countRegkeys();

    boolean isUser(String email);

    Date isRegkey(String regkey);

    UUID getUser(String email);

    boolean setUser(UUID uuid, Client client);

    boolean setUserServices(Client client);

    boolean setUser(Client client);

    int setUsers(Report report);

    int setRegkeys(Report report);

    int setSchedule(Report report);

    int setRizData(Report report);


    int countTest();

    int countTestMark();

    int countTestEmail();


    Map<String, Object> getTest();

    Map<String, Object> getTestMark();

    Map<String, Object> getTestEmail();

    Map<String, Object> getUsersList();

    Map<String, Object> getUserData(UUID id);

    Map<String, Object> getUserServices(UUID id);

    Map<String, Object> getUserServiceData(UUID id, int service);

    boolean deleteUserData(UUID id);


}