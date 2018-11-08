/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.service;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author IgorLysov
 */
public interface DateTimeService {
    String NAME = "gklients_DateTimeService";

    Date getCurrentDateTime();

    long getCurrentTimestamp();

    Date getStartOfDay(Date date);

    long getStartOfDay(long timestamp);

    Date getEndOfDay(Date date);

    long getEndOfDay(long timestamp);

    Date getStartOfToday();

    Date getEndOfToday();

    LocalDate makeLocalDate(Date date);

    Date getStartOfYesterday();

    ValidationCode validateWorkingDate(Date date) ;

    enum ValidationCode {
        HOLIDAY,
        MOVED_WEEKEND,
        WEEKEND,
        WORKING_DAY
    }

}