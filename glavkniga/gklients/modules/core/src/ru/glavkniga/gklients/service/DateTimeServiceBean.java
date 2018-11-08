/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.service;

import com.haulmont.cuba.core.global.TimeSource;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import ru.glavkniga.gklients.crud.Gget;
import ru.glavkniga.gklients.crudentity.DateException;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @author IgorLysov
 */
@Service(DateTimeService.NAME)
public class DateTimeServiceBean implements DateTimeService {

    @Inject
    private TimeSource timeSource;

    @Override
    public Date getCurrentDateTime() {
        return timeSource.currentTimestamp();
    }

    @Override
    public long getCurrentTimestamp() {
        return timeSource.currentTimeMillis();
    }

    @Override
    public Date getStartOfDay(Date date) {
        return DateUtils.truncate(date, Calendar.DATE);

    }

    @Override
    public long getStartOfDay(long timestamp) {
        Date date = DateUtils.truncate(new Date(timestamp), Calendar.DATE);
        return date.getTime();

    }

    @Override
    public Date getEndOfDay(Date date) {
        return DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.DATE), -1);
    }

    @Override
    public long getEndOfDay(long timestamp) {
        Date date = DateUtils.addMilliseconds(DateUtils.ceiling(new Date(timestamp), Calendar.DATE), -1);
        return date.getTime();
    }

    @Override
    public Date getStartOfToday() {
        return getStartOfDay(timeSource.currentTimestamp());
    }

    @Override
    public Date getEndOfToday() {
        return getEndOfDay(timeSource.currentTimestamp());
    }


    @Override
    public LocalDate makeLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        return zdt.toLocalDate();
    }

    public Date getStartOfYesterday() {
        Date date = getStartOfToday();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -1); //make yesterday
        return cal.getTime();
    }

    public ValidationCode validateWorkingDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //Check in DateExceptions table if the date is in exception list
        Gget gDateGet = new Gget(DateException.class);
        gDateGet.addFilterProperty("year", String.valueOf(cal.get(Calendar.YEAR)));
        gDateGet.addFilterProperty("month", String.valueOf(cal.get(Calendar.MONTH) + 1));//TODO replace +1 with correct month number
        gDateGet.addFilterProperty("day", String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
        gDateGet.addFilterProperty("region", "0");
        Map<String, Object> objMap = gDateGet.getObjects();
        if (objMap != null && objMap.size() > 0) { //if any date found in an exception dates
            for (Object object : objMap.values()) {
                DateException dateException = (DateException) object;
                int status = dateException.getStatus();
                if (status == 2) { //if found a date which is a holiday - don't need to send the distribution
                    return ValidationCode.HOLIDAY;
                }
                if (status == 4) {
                    return ValidationCode.MOVED_WEEKEND;
                }
            }
        } else { // if no dates found but it is a weekend
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                return ValidationCode.WEEKEND;
            }
        }
        return ValidationCode.WORKING_DAY;
    }

}