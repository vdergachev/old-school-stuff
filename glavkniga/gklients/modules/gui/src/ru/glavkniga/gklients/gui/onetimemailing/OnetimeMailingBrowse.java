/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.onetimemailing;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.MailingStatistics;
import ru.glavkniga.gklients.entity.OnetimeMailing;
import ru.glavkniga.gklients.enumeration.OnetimeMailingStatus;
import ru.glavkniga.gklients.service.MailingStatisticService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Igor Lysov
 */
public class OnetimeMailingBrowse extends AbstractLookup {

    @Inject
    private ComponentsFactory componentsFactory;

    @Named("onetimeMailingsTable")
    private GroupTable<OnetimeMailing> onetimeMailingsTable;

    @Named("onetimeMailingsDs")
    private GroupDatasource<OnetimeMailing, UUID> onetimeMailingsDs;

    @Named("statisticsBtn")
    private Button statisticsBtn;

    @Inject
    private MailingStatisticService mailingStatisticService;

    @Override
    public void init(Map<String, Object> params) {
        onetimeMailingsTable.addGeneratedColumn("cleanBody", entity -> {
            String body = entity.getBody();
            String cleanBody = body != null ? Jsoup.clean(body, Whitelist.none()) : "";
            String shortBody = cleanBody.length() > 50 ? cleanBody.substring(0, 50) + "..." : cleanBody;
            Label label = componentsFactory.createComponent(Label.class);
            label.setValue(shortBody);
            label.setDescription(cleanBody);
            return label;
        });
        onetimeMailingsTable.getColumn("cleanBody").setCaption(getMessage("cleanBodyCap"));
        onetimeMailingsTable.getColumn("cleanBody").setMaxTextLength(50);

        onetimeMailingsTable.addGeneratedColumn("clientsCount", entity -> {
            Set<Client> clients = entity.getClient();
            Label label = componentsFactory.createComponent(Label.class);
            label.setValue(clients.size());
            return label;
        });
        onetimeMailingsTable.getColumn("clientsCount").setCaption(getMessage("clientsCount"));

        onetimeMailingsDs.addItemChangeListener(e -> {
            statisticsBtn.setEnabled(onetimeMailingsTable.getSingleSelected() != null);
        });
    }


    public void onStatisticsBtnClick() {
        OnetimeMailing mailing = onetimeMailingsTable.getSingleSelected();
        if (mailing != null) {
            if (mailing.getStatus() != null && (mailing.getStatus() == OnetimeMailingStatus.Queued || mailing.getStatus() == OnetimeMailingStatus.Completed)) {
                MailingStatistics statistics = mailing.getMailingStatistics();
                openEditor(statistics, WindowManager.OpenType.DIALOG);
            } else {
                showNotification(getMessage("mailingNotYetSentMsg"));
            }
        }
    }
}