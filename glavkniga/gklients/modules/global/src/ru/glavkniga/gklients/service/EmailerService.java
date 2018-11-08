/*
 * Copyright (c) 2015 ru.glavkniga.gklients.service
 */
package ru.glavkniga.gklients.service;

import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.EmailTemplate;
import ru.glavkniga.gklients.entity.MagazineIssue;
import ru.glavkniga.gklients.enumeration.SysEmail;

import java.util.Map;
import java.util.UUID;

/**
 * @author LysovIA
 */
public interface EmailerService {
    String NAME = "gklients_EmailerService";


    void activateRegkey(UUID esId);

    void changePassword(Client client);

    void remindPassword(Client client);

    void notifyRiz(UUID esId);

    void changeEmail(Client client);

    void notifyNewIssue(Client client, MagazineIssue magazineIssue);

    String processTemplate(String template, Map<String, String> replacements);

    EmailTemplate loadTemplate(SysEmail sysEmail);

}
