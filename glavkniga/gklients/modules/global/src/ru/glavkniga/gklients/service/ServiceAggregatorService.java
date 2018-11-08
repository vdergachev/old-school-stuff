/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.service;

import ru.glavkniga.gklients.entity.ClientServiceAgregator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author LysovIA
 */
public interface ServiceAggregatorService {
    String NAME = "gklients_ServiceAggregatorService";

    Collection<ClientServiceAgregator> aggregateClientServices();
}