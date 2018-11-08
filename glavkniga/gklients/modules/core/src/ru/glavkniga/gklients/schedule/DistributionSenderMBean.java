/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.schedule;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.glavkniga.gklients.crud.Gget;
import ru.glavkniga.gklients.crudentity.DailyNewsDistribution;
import ru.glavkniga.gklients.crudentity.GKNews;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientDistributionSettings;
import ru.glavkniga.gklients.entity.MailingStatistics;
import ru.glavkniga.gklients.entity.OnetimeMailing;
import ru.glavkniga.gklients.enumeration.OnetimeMailingStatus;
import ru.glavkniga.gklients.mailing.MailMessage;
import ru.glavkniga.gklients.service.DateTimeService;
import ru.glavkniga.gklients.service.GkNewsProviderService;
import ru.glavkniga.gklients.service.MassMailingService;
import ru.glavkniga.gklients.service.NewsTemplateProcessorService;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Created by IgorLysov on 06.08.2017.
 */

//TODO this is very raw code and needs to be carefully checked. Awaiting for GkNewsProviderService to complete

@Component(DistributionSender.NAME)
public class DistributionSenderMBean implements DistributionSender {

    @Inject
    private NewsTemplateProcessorService templateProcessor;

    @Inject
    private GkNewsProviderService gkNewsProviderService;

    @Inject
    private DataManager dataManager;

    @Inject
    private MessageTools messageTools;

    @Inject
    private DateTimeService dts;

    @Inject
    private MassMailingService massMailingService;

    @Inject
    private Metadata metadata;

    private Logger log = LoggerFactory.getLogger(getClass());


    @Inject
    private Persistence persistence;


    @Override
    public String sendNewsDistribution() {
        StringBuilder resultString = new StringBuilder();
        Date sendingDate = dts.getStartOfYesterday();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormat.format(sendingDate);

        switch (dts.validateWorkingDate(sendingDate)) {
            case HOLIDAY:
                return "There was a holiday on " + dateString + " based on Date Exceptions. No need to send any news";
            case MOVED_WEEKEND:
                return "There was a moved weekend on " + dateString + "  based on Date Exceptions. No need to send any news";
            case WEEKEND:
                return "There was a weekend on " + dateString + ". No need to send any news";
            case WORKING_DAY: //just go on as it's ok
                break;
        }

        DailyNewsDistribution dnd = getDNDForDate(sendingDate);
        if (dnd == null) {
            dnd = metadata.create(DailyNewsDistribution.class);
            dnd.setDistributionDate(sendingDate);
            dnd.setIntroText("&nbsp;");
        }
        dnd.setDistributionTitle("Главная книга: Бухгалтерские новости от " + dateString); //TODO move to message pack

        List<GKNews> newsList = loadNewsListForDate(sendingDate);
        if (newsList == null || newsList.size() == 0) {
            return "No news to send for " + dateString;
        }

        List<GkNewsProviderService.GkNewsSet> newsSetList = gkNewsProviderService.provide(newsList);
        String emailBody;
        OnetimeMailing onetimeMailing = makeOnetimeMailingForDistribution(dnd.getDistributionTitle(), dnd.getDistributionDate());

        Set<Client> distributionAddressee = new HashSet<>();
        GkNewsProviderService.GkNewsSet gkNewsSet;

        int completed = 0;
        int failed = 0;
        for (int i = 0; i < newsSetList.size(); i++) {

            gkNewsSet = newsSetList.get(i);

            Client client = gkNewsSet.getClients().iterator().next();
            ClientDistributionSettings clientSettings =
                    (ClientDistributionSettings) EntityWorker.getEntityByFieldValue(
                            ClientDistributionSettings.class, "client.id", client.getId()
                    );


            emailBody = templateProcessor.process(clientSettings, gkNewsSet.getNews(), dnd);
            distributionAddressee.addAll(gkNewsSet.getClients());
            final List<String> emails = gkNewsSet.getClients().stream().map(Client::getEmail).collect(toList());
            final MailMessage message = new MailMessage()
                    .withSubject(dnd.getDistributionTitle())
                    .withBody(emailBody)
                    .withMessageId(String.valueOf(onetimeMailing.getId()));
            try {
                massMailingService.send(message, emails);

                log.warn("Message send to " + String.valueOf(emails.size()) + " emails ");

                completed += emails.size();
                resultString.append("Group ")
                        .append(i)
                        .append(" of total ")
                        .append(emails.size())
                        .append(" email sent\r\n");
            } catch (RemoteException remEx) {
                failed += emails.size();
                resultString.append("Exception sending distribution to group ")
                        .append(i)
                        .append(". ")
                        .append(emails.size())
                        .append(" emails affected \r\n");
            }
        }

        onetimeMailing.setClient(distributionAddressee);
        onetimeMailing.setSendingDate(dts.getCurrentDateTime());
        MailingStatistics statistics = onetimeMailing.getMailingStatistics();

        //TODO define how to count emails which not even been queued emails to

        statistics.setPlanned(distributionAddressee.size());
        statistics.setCompleted(0);
        statistics.setFailed(0);
        statistics.setSending(0);

        for (GKNews news : newsList) {
            news.setIsSent(true);
        }

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            em.persist(onetimeMailing);
            em.persist(statistics);
            newsList.forEach(em::merge);
            tx.commit();
        }
        return resultString.toString();
    }


    private DailyNewsDistribution getDNDForDate(Date distributionDate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String distributionDateString = dateFormat.format(distributionDate);

        List<DailyNewsDistribution> dndList = new ArrayList<>();

        Gget gget = new Gget(DailyNewsDistribution.class);
        gget.addFilterProperty("distributionDate", distributionDateString);
        Map<String, Object> entityMap = gget.getObjects();

        if (entityMap != null && entityMap.size() > 0) {
            entityMap.forEach((str, obj) -> dndList.add((DailyNewsDistribution) obj));
        } else return null;
        return dndList.get(0);
    }

    private List<GKNews> loadNewsListForDate(Date distributionDate) {
        Date dateFrom = dts.getStartOfDay(distributionDate);
        Date dateTo = dts.getEndOfDay(distributionDate);

        LoadContext.Query query = LoadContext.createQuery("" +
                "select n " +
                "from gklients$GKNews n " +
                "where " +
                "n.newsDate BETWEEN  :news_from and :news_end " +
                "and (n.isSent IS null or n.isSent = false)")
                .setParameter("news_from", dateFrom)
                .setParameter("news_end", dateTo);

        LoadContext<GKNews> loadContext = LoadContext.create(GKNews.class)
                .setQuery(query)
                .setView("gKNews-full-view");
        return dataManager.loadList(loadContext);

    }

    private OnetimeMailing makeOnetimeMailingForDistribution(String subject, Date sendingDate) {
        OnetimeMailing mailing = metadata.create(OnetimeMailing.class);
        mailing.setStatus(OnetimeMailingStatus.Queued);
        mailing.setSendingDate(sendingDate);
        mailing.setSubject(subject);
        mailing.setBody("Содержимое новостной рассылки разное для разных клиентов. Тело письма не сохраняется.");
        MailingStatistics statistics = metadata.create(MailingStatistics.class);
        statistics.setOnetimeMailing(mailing);
        mailing.setMailingStatistics(statistics);
        return mailing;
    }

//    private void persist(Entity entity) {
//        persistList(ImmutableList.of(entity));
//    }

//    private void persistList(List<Entity> entityList) {
//        CommitContext commitContext = new CommitContext(entityList);
//        dataManager.commit(commitContext);
//    }


}


