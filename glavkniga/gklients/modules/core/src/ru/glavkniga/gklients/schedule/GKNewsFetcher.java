/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.schedule;

/**
 * Created by IgorLysov on 07.07.2017.
 */


public interface GKNewsFetcher {

    String NAME = "gklients_GKNewsFetcher";

    String fetchToday();
    String fetchThisWeek();
    String fetchLast30Days() ;
}
