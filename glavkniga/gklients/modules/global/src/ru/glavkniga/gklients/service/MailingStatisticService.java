/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.service;

import ru.glavkniga.gklients.entity.MailingStatistics;

/**
 * @author Хомяк
 */
public interface MailingStatisticService {
    String NAME = "gklients_MailingStatisticService";

    boolean refreshStatistics(MailingStatistics statistics);

}