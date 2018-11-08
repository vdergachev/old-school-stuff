/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.distribution;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import ru.glavkniga.gklients.entity.Distribution;
import ru.glavkniga.gklients.entity.DistributionSubscription;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

/**
 * @author Igor Lysov
 */
public class DistributionBrowse extends AbstractLookup {

    @Named("distributionsTable")
    private GroupTable<Distribution> distributionsTable;

    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    public void init(Map<String, Object> params) {

        distributionsTable.addGeneratedColumn("cleanBody", entity -> {
            String body = entity.getContent();
            String cleanBody = body != null ? Jsoup.clean(body, Whitelist.none()) : "";
            String shortBody = cleanBody.length() > 50 ? cleanBody.substring(0, 50) + "..." : cleanBody;
            Label label = componentsFactory.createComponent(Label.class);
            label.setValue(shortBody);
            label.setDescription(cleanBody);
            return label;
        });
        distributionsTable.getColumn("cleanBody").setCaption(getMessage("cleanBodyCap"));
        distributionsTable.getColumn("cleanBody").setMaxTextLength(50);

        distributionsTable.addGeneratedColumn("clientsCount", entity -> {
            List<DistributionSubscription> subscriptionList = entity.getDistributionSubscription();
            Label label = componentsFactory.createComponent(Label.class);
            label.setValue(subscriptionList.size());
            return label;
        });
        distributionsTable.getColumn("clientsCount").setCaption(getMessage("clientsCount"));
    }
}