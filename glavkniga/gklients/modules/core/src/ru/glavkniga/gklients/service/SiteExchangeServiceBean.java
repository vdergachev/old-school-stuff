/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.service;

import org.springframework.stereotype.Service;
import ru.glavkniga.gklients.crud.Gget;
import ru.glavkniga.gklients.crudentity.SiteExchange;

import java.util.Map;

/**
 * @author Хомяк
 */
@Service(SiteExchangeService.NAME)
public class SiteExchangeServiceBean implements SiteExchangeService {

    public String collectData() {

        Gget siteExGet = new Gget(SiteExchange.class);
        Map<String, Object> siteExchangeObjects = siteExGet.getObjects();
        if (siteExchangeObjects != null && siteExchangeObjects.size() > 0)
            return String.valueOf(siteExchangeObjects.size());
        else
            return "No objects fetched";
    }
}