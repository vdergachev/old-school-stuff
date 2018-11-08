/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.service;

import ru.glavkniga.gklients.mailing.Context;

import java.util.UUID;

/**
 * @author IgorLysov
 */
public interface TemplateProcessService {
    String NAME = "gklients_TemplateProcessService";

    //    MailMessage processTemplate(EmailTemplate template, Context context);
    String processTemplate(String template, Context context);
    String processTemplate(String template, UUID clientId);

    Context loadContextByClientId(UUID clientId);

    Boolean validateTemplate(String template);

}