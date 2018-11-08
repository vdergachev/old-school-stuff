/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.schedule;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.ValueLoadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.glavkniga.gklients.crud.Gget;
import ru.glavkniga.gklients.crud.Operation;
import ru.glavkniga.gklients.crudentity.GKNews;
import ru.glavkniga.gklients.crudentity.GKNewsImage;
import ru.glavkniga.gklients.service.DateTimeService;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LysovIA on 07.07.2017.
 */

@Component(GKNewsFetcher.NAME)
public class GKNewsFetcherMBean implements GKNewsFetcher {


    @Inject
    private DataManager dataManager;

    private Map<Integer, GKNews> fetchNewsBetweenDates(long starttime, long endtime) {

        //first load list of existing news
        ValueLoadContext context = ValueLoadContext.create()
                .setQuery(ValueLoadContext.createQuery("select n.id, n.siteId from gklients$GKNews n"))
                .addProperty("id")
                .addProperty("siteId");
        List<KeyValueEntity> existingNewsList = dataManager.loadValues(context);

        //then make list of SiteIDs of news already collected from website
        List<Integer> newsSiteIds = new ArrayList<>();
        if (existingNewsList != null && existingNewsList.size() > 0) {
            for (KeyValueEntity entity : existingNewsList) {
                try {
                    newsSiteIds.add((Integer) entity.getValue("siteId"));
                } catch (Exception ignored) {
                }
            }
        }

        //Now collect news from website for the given date range
        DateTimeService dts = AppBeans.get(DateTimeService.NAME);

        Gget gNewsGet = new Gget(GKNews.class);
        gNewsGet.addFilterField("published", "1", Operation.EQUAL);
        long startOfDayInSec = dts.getStartOfDay(starttime) / 1000;
        long endOfDayInSec = dts.getEndOfDay(endtime) / 1000;
        gNewsGet.addFilterField("startstamp", String.valueOf(startOfDayInSec), Operation.MORE_EQUAL);
        gNewsGet.addFilterField("startstamp", String.valueOf(endOfDayInSec), Operation.LESS);
        Map<String, Object> todaysNews = gNewsGet.getObjects();
        Map<Integer, GKNews> newsLine = new HashMap<>();

        if (todaysNews != null && todaysNews.size() > 0) {
            todaysNews.forEach((key, entity) -> {
                GKNews news = (GKNews) entity;
                Integer siteId = news.getSiteId();
                //making sure news not exist in the database
                if (!newsSiteIds.contains(siteId)) {
                    newsLine.put(siteId, news);
                }
            });
        }
        return newsLine;
    }

    private Map<Integer, GKNewsImage> fetchImagesForNews(Map<Integer, GKNews> newsLine) {
        Map<Integer, GKNewsImage> newsImages = new HashMap<>();
        String ids = newsLine.keySet().stream().map(Object::toString).collect(Collectors.joining(","));
        Gget gNewsImagesGet = new Gget(GKNewsImage.class);
        gNewsImagesGet.addFilterField("news_id", ids, Operation.IN);
        Map<String, Object> pix = gNewsImagesGet.getObjects();
        if (pix != null && pix.size() > 0) {
            pix.forEach((key, entity) -> {
                GKNewsImage newsImage = (GKNewsImage) entity;
                Integer siteId = newsImage.getSiteId();
                newsImage.setGkNews(newsLine.get(siteId));
                newsImages.put(siteId, newsImage);
            });
        }
        return newsImages;
    }


    // This method is to be executed via Scheduler
    @Override
    public String fetchToday() {
        DateTimeService dts = AppBeans.get(DateTimeService.NAME);
        return fetchSinceDate(dts.getCurrentDateTime());
    }

    // This method is to be executed via Scheduler
    public String fetchThisWeek() {
        DateTimeService dts = AppBeans.get(DateTimeService.NAME);
        // set the date
        Calendar cal = Calendar.getInstance();
        cal.setTime(dts.getCurrentDateTime());
        // "calculate" the start date of the week
        Calendar first = (Calendar) cal.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        return fetchSinceDate(first.getTime());
    }

    // This method is to be executed via Scheduler
    public String fetchLast30Days() {
        DateTimeService dts = AppBeans.get(DateTimeService.NAME);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dts.getCurrentDateTime());
        cal.add(Calendar.DATE, -30);
        return fetchSinceDate(cal.getTime());
    }


    private String fetchSinceDate(Date dateStart) {
        StringBuilder returnValue = new StringBuilder();

        DateTimeService dts = AppBeans.get(DateTimeService.NAME);
        Date currDate = dts.getCurrentDateTime();

        Map<Integer, GKNews> newsLine = this.fetchNewsBetweenDates(dateStart.getTime(), currDate.getTime());
        if (newsLine != null && newsLine.size() > 0) {
            Map<Integer, GKNewsImage> newsImages = fetchImagesForNews(newsLine);
            EntityWorker.persist(newsLine.values());
            returnValue
                    .append(newsLine.size())
                    .append(" news ");

            if (newsImages != null && newsImages.size() > 0) {
                EntityWorker.persist(newsImages.values());
                returnValue
                        .append(" and ")
                        .append(newsImages.size())
                        .append(" images ");
            }
        } else {
            returnValue
                    .append("Probably all news already collected. No news or images ");
        }
        return returnValue.append("fetched.").toString();
    }


}
