/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.service;

import com.haulmont.cuba.core.Persistence;
import org.springframework.stereotype.Service;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ElverSubscription;
import ru.glavkniga.gklients.mailing.Context;
import ru.glavkniga.gklients.mailing.TemplateProcessor;
import ru.glavkniga.gklients.schedule.EntityWorker;

import javax.inject.Inject;
import java.util.UUID;

/**
 * @author IgorLysov
 */
@Service(TemplateProcessService.NAME)
public class TemplateProcessServiceBean implements TemplateProcessService {

    @Inject
    private TemplateProcessor templateProcessor;

    @Inject
    private Persistence persistence;

    @Override
    public String processTemplate(String template, Context context) {

        return templateProcessor.process(template, context);

    }

    @Override
    public String processTemplate(String template, UUID clientId) {
        Context context = this.loadContextByClientId(clientId);
        return processTemplate(template, context);
    }

    public Context loadContextByClientId(UUID clientId) {
        Context context = new Context();
        Client client = (Client) EntityWorker.getEntity(Client.class, clientId);

        if (client != null) {
            context.withClient(client);
        }

        ElverSubscription subscription =
                (ElverSubscription) EntityWorker.getEntityByFieldValue(
                        ElverSubscription.class,
                        "client.id",
                        clientId,
                        "elverSubscription-token-view"
                );

        if (subscription != null) {
            context
                    .withElverSubscription(subscription)
                    .withMagazineIssueStart(subscription.getIssueStart())
                    .withMagazineIssueEnd(subscription.getIssueEnd());
        }
        return context;
    }

    @Override
    public Boolean validateTemplate(String template) {

        return templateProcessor.validate(template);
    }

}