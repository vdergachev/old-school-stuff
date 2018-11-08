/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.service;

import ru.glavkniga.gklients.crudentity.DailyNewsDistribution;
import ru.glavkniga.gklients.crudentity.GKNews;
import ru.glavkniga.gklients.entity.ClientDistributionSettings;

import java.util.List;

/**
 * @author IgorLysov
 */
public interface NewsTemplateProcessorService {
    String NAME = "gklients_NewsTemplateProcessorService";

    String process(ClientDistributionSettings settings, List<GKNews> newsList, DailyNewsDistribution dnd);
}