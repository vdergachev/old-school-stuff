/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.service;

import ru.glavkniga.gklients.entity.ElverSubscription;
import ru.glavkniga.gklients.entity.MagazineIssue;
import ru.glavkniga.gklients.entity.Schedule;

import java.util.Date;
import java.util.List;

/**
 * @author LysovIA
 */
public interface DefineIssuesRangeService {
    String NAME = "gklients_DefineIssuesRangeService";

    public List<MagazineIssue> getMagazineIssuesForES(ElverSubscription subscription);
    public List<MagazineIssue> getMagazineIssuesForDates(Date dateFrom, Date dateTo);
    public List<MagazineIssue>getMagazineIssuesInBetween(MagazineIssue issueStart, MagazineIssue issueEnd);
    public Date getDateFrom(ElverSubscription subscription);
    public Date getDateTo(ElverSubscription subscription);
   // public List<Schedule> getSchedulesForES(ElverSubscription subscription);
}