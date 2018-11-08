/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.mailingstatistics;

import com.haulmont.cuba.gui.components.AbstractEditor;
import ru.glavkniga.gklients.entity.MailingStatistics;
import ru.glavkniga.gklients.service.MailingStatisticService;

import javax.inject.Inject;

/**
 * @author Хомяк
 */
public class MailingStatisticsEdit extends AbstractEditor<MailingStatistics> {

    @Inject
    private MailingStatisticService mailingStatisticService;

    public void onBtnUpdateClick() {
        MailingStatistics statistics = getItem();
        mailingStatisticService.refreshStatistics(statistics);
    }
}