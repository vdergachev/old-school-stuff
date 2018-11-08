/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.gui.clientservice;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CollectionDatasourceImpl;
import ru.glavkniga.gklients.entity.ClientServiceAgregator;
import ru.glavkniga.gklients.service.ServiceAggregatorService;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LysovIA on 20.01.2017.
 */
public class ServiceAggregatorDatasource extends CollectionDatasourceImpl<ClientServiceAgregator, UUID> {

    private ServiceAggregatorService service = AppBeans.get(ServiceAggregatorService.NAME);

    @Override
    protected void loadData(Map<String, Object> params) {
        data.clear();
        dataLoadError = null;
        try {
            Collection<ClientServiceAgregator> collection = service.aggregateClientServices();
            Iterator<ClientServiceAgregator> iterator = collection.iterator();
            while (iterator.hasNext()) {
                ClientServiceAgregator item = iterator.next();
                data.put(item.getId(), item);
            }
        } catch (Exception e) {
            dataLoadError = e;
        }

    }
}
