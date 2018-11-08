/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.distributionSubscriptionComposition;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import ru.glavkniga.gklients.crudentity.DailyNewsDistribution;
import ru.glavkniga.gklients.crudentity.GKNews;
import ru.glavkniga.gklients.entity.*;
import ru.glavkniga.gklients.enumeration.SubscriptionStatus;
import ru.glavkniga.gklients.service.DateTimeService;
import ru.glavkniga.gklients.service.NewsTemplateProcessorService;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Igor Lysov
 */
public class DistributionSubscriptionComposition extends AbstractLookup {

    @Named("clientsTable")
    private GroupTable<ClientDistributionSettings> clientsTable;

    @Named("clientDistributionSettingsDs")
    private GroupDatasource<ClientDistributionSettings, UUID> clientDistributionSettingsDs;

    @Named("createBtn")
    private Button createBtn;

    @Named("multiselectBtn")
    private PopupButton multiselectBtn;

    @Inject
    private Metadata metadata;

    @Named("gKNewsDs")
    private CollectionDatasource<GKNews, UUID> gKNewsDs;

    @Inject
    protected ExportDisplay exportDisplay;

    @Inject
    private NewsTemplateProcessorService nts;

    @Inject
    private DataManager dataManager;

    @Inject
    private DateTimeService dts;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Collection<Action> actions = clientsTable.getActions();

        actions.forEach(action -> {
            if (action instanceof CreateAction) {
                ((CreateAction) action).setWindowId("gklients$Client.EmailSettings.edit");
            }
            if (action instanceof EditAction) {
                ((EditAction) action).setWindowId("gklients$Client.EmailSettings.edit");
            }
        });

        clientDistributionSettingsDs.addItemChangeListener(event -> {
            ClientDistributionSettings newItem = clientDistributionSettingsDs.getItem();
            if (newItem != null) {
                createBtn.setEnabled(true);
                multiselectBtn.setEnabled(true);
            } else {
                createBtn.setEnabled(false);
                multiselectBtn.setEnabled(false);
            }
        });

    }

    public void newClientDistributionSubscription() {
        ClientDistributionSettings cds = clientDistributionSettingsDs.getItem();
        if (cds != null) {
            DistributionSubscription ds = metadata.create(DistributionSubscription.class);
            ds.setClient(cds.getClient());
            ds.setDateBegin(new Date());
            ds.setDateStatusUpdate(new Date());
            ds.setStatus(SubscriptionStatus.subscribed);
            openEditor(ds, WindowManager.OpenType.DIALOG);
        }
    }

    public void onAddSubscription(Component source) {
        Set<ClientDistributionSettings> cds = clientsTable.getSelected();
        Set<Client> clients = new HashSet<>();

        if (cds != null) {
            cds.forEach(clientDistributionSettings -> clients.add(clientDistributionSettings.getClient()));
            Map<String, Object> params = new HashMap<>();
            Distribution distribution = metadata.create(Distribution.class);
            params.put("CLIENTS", clients);
            openEditor(distribution, WindowManager.OpenType.DIALOG, params);
        }
    }

    public void onMakeOntimeSending(Component source) {
        Set<ClientDistributionSettings> cds = clientsTable.getSelected();
        Set<Client> clients = new HashSet<>();

        if (cds != null) {
            cds.forEach(clientDistributionSettings -> clients.add(clientDistributionSettings.getClient()));
            Map<String, Object> params = new HashMap<>();
            OnetimeMailing mailing = metadata.create(OnetimeMailing.class);
            params.put("CLIENTS", clients);
            openEditor(mailing, WindowManager.OpenType.DIALOG, params);
        }
    }

    private List<GKNews> loadNewsList(Date sendingDate) {
        Date dateFrom = dts.getStartOfDay(sendingDate);
        Date dateTo = dts.getEndOfDay(sendingDate);
        LoadContext.Query query = new LoadContext
                .Query("select n from gklients$GKNews n where n.newsDate Between :dateFrom and :dateTo")
                .setParameter("dateFrom", dateFrom)
                .setParameter("dateTo", dateTo);
        LoadContext<GKNews> loadContext = LoadContext.create(GKNews.class)
                .setQuery(query)
                .setView("gKNews-full-view");
        return dataManager.loadList(loadContext);
    }

    public void onPreviewButtonClick(Component source) {

        Date sendingDate = dts.getStartOfYesterday();

        switch (dts.validateWorkingDate(sendingDate)) {
            case HOLIDAY:
                showNotification(getMessage("holidayYesterday"));
                return;
            case MOVED_WEEKEND:
                showNotification(getMessage("movedWeekendYesterday"));
                return;
            case WEEKEND:
                showNotification(getMessage("weekendYesterday"));
                return;
            case WORKING_DAY:
                //it's ok, go on sending;
                break;
        }

        ClientDistributionSettings settings = clientDistributionSettingsDs.getItem();
        if (settings == null)
            return;

        List<GKNews> newsList = loadNewsList(sendingDate);
        if (newsList == null || newsList.isEmpty()) {
            showNotification(getMessage("noNews"));
        } else {
            List<GKNews> clientNewsList = new ArrayList<>();
            for (GKNews news : newsList) {
                if (isNewsForClientSettings(news, settings)) {
                    clientNewsList.add(news);
                }
            }

            if (clientNewsList.isEmpty()){
                showNotification(getMessage("noNewsFits"));
                return;
            }

            DailyNewsDistribution dnd = metadata.create(DailyNewsDistribution.class);
            dnd.setDistributionDate(new Date());
            dnd.setDistributionTitle("My Very beautiful distribution");
            // dnd.setIntroText("This is the intro text so be careful with it");
            String emailContent = nts.process(settings, clientNewsList, dnd);

            if (emailContent != null) {
                ByteArrayDataProvider dataProvider = new ByteArrayDataProvider(emailContent.getBytes(StandardCharsets.UTF_8));
                exportDisplay.show(dataProvider, "email-preview.html", ExportFormat.HTML);
            }
        }
    }


    /*
    * Есть настройки новостей и настройки подписчика - по 10 флагов и там и тут.
    * Флаги абсолютно идентичны по смыслу и наименованию.
    * Флаги разбиваются на 3 группы:
    * * Налоги - usno osno eshn envd psn
    * * Виды организации company ip civil
    * * наличие работников withWorkers withoutWorkers
    * По умолчанию считается, что и для новости, и для подписчика:
    * * Флаги налогов могут быть выставлены в любой комбинации
    * * Флаги организации и работников комбинируются следующим образом
    * * * Если выставлены только company или только civil, флаги withWorkers withoutWorkers не выставляются (предметная область подразумевает,
    * что у компаний всегда есть работники, а у физлиц работников всегда нет)
    * * * Если выставлен ip - можно выставить withWorkers withoutWorkers или не выставлять их
    *
    * Требования на соответствие новости и настройкам подписчика следующие
    * Если есть совпадения в первой группе и нет противоречий во второй и третьей, новость включается в рассылку.
    * Ещё новости, помеченные для физлиц, включаются в рассылку независимо от всего остального.
    * Детально:
    * * Если в новости выставлен флаг civil - включаем и не проверяем дальше.
    * * Хотя бы один одинаковый налог должен быть выставлен всегда. Иначе не включаем
    * * Если совпала company (с обеих сторон выставлена) - включаем.
    * * Если совпала по ИП - проверяем работников. Чтобы новость попала в рассылку, либо хотя бы один флаг должен совпасть, либо с одной из сторон флаги не выставлены.
    * */
    private boolean isNewsForClientSettings(GKNews news, ClientDistributionSettings settings) {

        // *** Below works, try to simplify ***
//        // Validate if taxes matches
//        if ((news.getTaxesAsByte() & settings.getTaxesAsByte()) != 0) {
//            //validate if organizations matches
//            if ((news.getOrgsAsByte() & settings.getOrgsAsByte()) != 0) {
//                //validate if client set IP on and we need to check workers
//                if (settings.getIsCompany() != null && !settings.getIsCompany()) {
//                    //validate if client set only one workers flag and we need to check it
//                    if (settings.getWorkersAsByte() != 0
//                            && settings.getWorkersAsByte() != 3
//                            && news.getWorkersAsByte() != 0
//                            && news.getWorkersAsByte() != 3) {
//                        //validate if workers matches
//                        if ((settings.getWorkersAsByte() & news.getWorkersAsByte()) != 0) {
//                            return true;
//                        } else return false;
//                    } else return true;
//                } else return true;
//            } else return false;
//        } else return false;
//    }

        //optimized by IDE, don't try to understand it!
        return news.getIsCivil() != null && news.getIsCivil()  //  news marked as for civils are going to anyone
                || (news.getTaxesAsByte() & settings.getTaxesAsByte()) != 0
                && (news.getIsCompany() != null
                && settings.getIsCompany() != null
                && settings.getIsCompany()
                && news.getIsCompany()
                || news.getIsIp() != null
                && settings.getIsIp() != null
                && settings.getIsIp()
                && news.getIsIp()
                && (news.getWorkersAsByte() == 0
                || settings.getWorkersAsByte() == 0
                || (news.getWorkersAsByte() & settings.getWorkersAsByte()) != 0));
    }
}