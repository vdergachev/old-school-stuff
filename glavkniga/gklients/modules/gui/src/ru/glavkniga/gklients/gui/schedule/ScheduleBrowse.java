/*
 * Copyright (c) 2015 ru.glavkniga.gklients.gui.schedule
 */
package ru.glavkniga.gklients.gui.schedule;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import ru.glavkniga.gklients.entity.Schedule;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @author LysovIA
 */
public class ScheduleBrowse extends AbstractLookup {
    @Inject
    protected Table scheduleTable;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Column(name = "DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Override
    public void init(Map<String, Object> params) {

        final int constIssueStartWeekDay1 = Calendar.MONDAY;
        final int constIssueStartWeekDay2 = Calendar.WEDNESDAY;
        final int constIssueFinishWeekDay = Calendar.WEDNESDAY;

        final int constIssueSignWeekDay = Calendar.FRIDAY;
        final int constPrintingRecevieWeekDay = Calendar.MONDAY;
        final int constSiteUploadWeekDay = Calendar.WEDNESDAY;
        final int constDeliveryDateWeekDay = Calendar.MONDAY;


        scheduleTable.addGeneratedColumn("cDeliveryDate", new Table.PrintableColumnGenerator<Schedule,String>() {

            private String getFormattedValue(Schedule schedule){
                String value = "";
                Date date = schedule.getDeliveryDate();
                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    if (day != constDeliveryDateWeekDay) {
                        value = new SimpleDateFormat("dd.MM EEEEE").format(date);
                    } else {
                        value = new SimpleDateFormat("dd.MM").format(date);
                    }
                }
                return value;
            }

            @Override
            public Component generateCell(Schedule schedule) {
                String value = getFormattedValue(schedule);
                Label label = componentsFactory.createComponent(Label.class);
                label.setValue(value);
                return label;
            }

            @Override
            public String getValue(Schedule schedule) {
            return getFormattedValue(schedule);
            }
        });

        scheduleTable.addGeneratedColumn("cIssueFinish", new Table.PrintableColumnGenerator<Schedule,String>() {

            private String getFormattedValue(Schedule schedule){
                String value = "";
                Date date = schedule.getIssueFinish();
                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    if (day != constIssueFinishWeekDay) {
                        value = new SimpleDateFormat("dd.MM EEEEE").format(date);
                    } else {
                        value = new SimpleDateFormat("dd.MM").format(date);
                    }
                }
                return value;
            }

            @Override
            public Component generateCell(Schedule schedule) {
                String value = getFormattedValue(schedule);
                Label label = componentsFactory.createComponent(Label.class);
                label.setValue(value);
                return label;
            }

            @Override
            public String getValue(Schedule schedule) {
                return getFormattedValue(schedule);
            }

        });

        scheduleTable.addGeneratedColumn("cIssueStart", new Table.PrintableColumnGenerator<Schedule,String>() {

            private String getFormattedValue(Schedule schedule){
                String value = "";
                Date date = schedule.getIssueStart();
                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    if (day != constIssueStartWeekDay1 && day != constIssueStartWeekDay2) {
                        value = new SimpleDateFormat("dd.MM EEEEE").format(date);
                    } else {
                        value = new SimpleDateFormat("dd.MM").format(date);
                    }
                }
                return value;
            }

            @Override
            public String getValue(Schedule schedule) {
                return getFormattedValue(schedule);
            }


            @Override
            public Component generateCell(Schedule schedule) {
                String value = getFormattedValue( schedule);
                Label label = componentsFactory.createComponent(Label.class);
                label.setValue(value);
                return label;
            }
        });

        scheduleTable.addGeneratedColumn("cIssueSign", new Table.PrintableColumnGenerator<Schedule,String>() {

            private String getFormattedValue(Schedule schedule){
                String value = "";
                Date date = schedule.getIssueSign();
                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    if (day != constIssueSignWeekDay) {
                        value = new SimpleDateFormat("dd.MM EEEEE").format(date);
                    } else {
                        value = new SimpleDateFormat("dd.MM").format(date);
                    }
                }
                return value;
            }
            @Override
            public String getValue(Schedule schedule) {
                return getFormattedValue(schedule);
            }
            @Override
            public Component generateCell(Schedule schedule) {
                String value = getFormattedValue(schedule);
                Label label = componentsFactory.createComponent(Label.class);
                label.setValue(value);
                return label;
            }
        });

        scheduleTable.addGeneratedColumn("cPrintingRecevie", new Table.PrintableColumnGenerator<Schedule,String>() {

            private String getFormattedValue(Schedule schedule){
                String value = "";
                Date date = schedule.getPrintingRecevie();
                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    if (day != constPrintingRecevieWeekDay) {
                        value = new SimpleDateFormat("dd.MM EEEEE").format(date);
                    } else {
                        value = new SimpleDateFormat("dd.MM").format(date);
                    }
                }
                return value;
            }
            @Override
            public String getValue(Schedule schedule) {
                return getFormattedValue(schedule);
            }
            @Override
            public Component generateCell(Schedule schedule) {
                String value = getFormattedValue(schedule);
                Label label = componentsFactory.createComponent(Label.class);
                label.setValue(value);
                return label;
            }
        });


        scheduleTable.addGeneratedColumn("cSiteUpload", new Table.PrintableColumnGenerator<Schedule,String>() {

            private String getFormattedValue(Schedule schedule){
                String value = "";
                Date date = schedule.getSiteUpload();
                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    if (day != constSiteUploadWeekDay) {
                        value = new SimpleDateFormat("dd.MM EEEEE").format(date);
                    } else {
                        value = new SimpleDateFormat("dd.MM").format(date);
                    }
                }
                return value;
            }
            @Override
            public String getValue(Schedule schedule) {
                return getFormattedValue(schedule);
            }
            @Override
            public Component generateCell(Schedule schedule) {
                String value = getFormattedValue(schedule);
                Label label = componentsFactory.createComponent(Label.class);
                label.setValue(value);
                return label;
            }
        });

        String msg = getMessage("deliveryDate");
        scheduleTable.getColumn("cDeliveryDate").setCaption(msg);


        msg = getMessage("issueSign");
        scheduleTable.getColumn("cIssueSign").setCaption(msg);

        msg = getMessage("siteUpload");
        scheduleTable.getColumn("cSiteUpload").setCaption(msg);

        msg = getMessage("printingRecevie");
        scheduleTable.getColumn("cPrintingRecevie").setCaption(msg);

        msg = getMessage("issueStart");
        scheduleTable.getColumn("cIssueStart").setCaption(msg);

        msg = getMessage("issueFinish");
        scheduleTable.getColumn("cIssueFinish").setCaption(msg);
    }


}