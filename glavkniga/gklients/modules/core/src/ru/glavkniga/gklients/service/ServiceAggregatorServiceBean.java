/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.stereotype.Service;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientService;
import ru.glavkniga.gklients.entity.ClientServiceAgregator;

import javax.inject.Inject;
import java.util.*;

/**
 * @author LysovIA
 */
@Service(ServiceAggregatorService.NAME)
public class ServiceAggregatorServiceBean implements ServiceAggregatorService {

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    public Collection<ClientServiceAgregator> aggregateClientServices() {
        Map<Client, ClientServiceAgregator> result = new HashMap<>();
        List<ClientService> list;
        Transaction tx = persistence.createTransaction();
        TypedQuery<ClientService> query;
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("Select s FROM gklients$ClientService s\n" +
                            "where s.deleteTs is null "
                    , ClientService.class);
            query.setViewName("clientService-extended");
            list = query.getResultList();
            tx.commit();
        } finally {
            tx.end();
        }
        for (ClientService cs : list) {
            ClientServiceAgregator item = metadata.create(ClientServiceAgregator.class);
            if (result.containsKey(cs.getClient())) {
                item = result.get(cs.getClient());
            } else {
                item.setClient(cs.getClient().getId());
                item.setEmail(cs.getClient().getEmail());
            }

            item.setClient(cs.getClient().getId());
            item.setEmail(cs.getClient().getEmail());

            String serviceId = cs.getService();
            switch (serviceId) {
                case "1":
                    item.setService1(cs.getActivationDate());
                    break;
                case "2":
                    item.setService2(cs.getActivationDate());
                    break;
                case "3":
                    item.setService3(cs.getActivationDate());
                    break;
                case "4":
                    item.setService4(cs.getActivationDate());
                    break;
                case "5":
                    item.setService5(cs.getActivationDate());
                    break;
            }
            result.put(cs.getClient(), item);
        }
        return new ArrayList<>(result.values());
    }
}