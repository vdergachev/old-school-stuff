/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.gui.screens;

import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import ru.glavkniga.gklients.entity.Riz;
import ru.glavkniga.gklients.entity.RizContract;
import ru.glavkniga.gklients.service.DateTimeService;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author LysovIA
 */
public class Regkeyactreportexecutor extends AbstractWindow {

    @Named("rizDs")
    CollectionDatasource<Riz, UUID> rizDs;

    @Named("rizSelectedDs")
    Datasource<Riz> rizSelectedDs;

    @Named("dfFrom")
    DateField dfFrom;

    @Named("dfTo")
    DateField dfTo;

    @Named("lfRiz")
    LookupField lfRiz;

    @Inject
    protected ReportGuiManager reportGuiManager;
    @Inject
    protected UserSessionSource userSessionSource;

    @Inject
    private DateTimeService dts;

    @Override
    public void init(Map<String, Object> params) {
        dfFrom.addValueChangeListener(e -> {
                    Map<String, Object> dsParam = new HashMap<>();
                    Date dFrom = dfFrom.getValue();
                    if (dFrom != null) {
                        dsParam.put("dateFrom", dts.getStartOfDay(dFrom));
                    }
                    Date dTo = dfTo.getValue();
                    if (dTo != null) {
                        dsParam.put("dateTo", dts.getEndOfDay(dTo));
                    }
                    rizDs.refresh(dsParam);
                }
        );

        dfTo.addValueChangeListener(e -> {
                    Map<String, Object> dsParam = new HashMap<>();
                    Date dFrom = dfFrom.getValue();
                    if (dFrom != null) {
                        dsParam.put("dateFrom", dts.getStartOfDay(dFrom));
                    }
                    Date dTo = dfTo.getValue();
                    if (dTo != null) {
                        dsParam.put("dateTo", dts.getEndOfDay(dTo));
                    }
                    rizDs.refresh(dsParam);
                }
        );
        dfFrom.setValue(dts.getStartOfDay(new Date()));
        dfTo.setValue(dts.getEndOfDay(new Date()));
    }


    public void onBcancelClick() {
        this.close(Window.CLOSE_ACTION_ID);
    }


    @Inject
    protected TimeSource timeSource;

    public void onBokClick() {
        Date dFrom = dts.getStartOfDay(dfFrom.getValue());
        Date dTo = dts.getEndOfDay(dfTo.getValue());
        Riz riz = lfRiz.getValue();

        if (validateFields(dFrom, dTo, riz)) {
            Map<String, Object> params = makeParams(dFrom, dTo, riz);
            String outputFileName = getFileName(riz);
            List<Report> reportCollection = reportGuiManager.getAvailableReports(
                    null,
                    userSessionSource.getUserSession().getUser(),
                    null
            );
            if (reportCollection.size() > 0) {
                for (Report report : reportCollection) {
                    if (report.getName().equals("regkey_act")) {
                        reportGuiManager.printReport(report,
                                params,
                                null,
                                outputFileName,
                                null);
                    }
                }
            }
        }

    }

    private String getFileName(Riz riz) {
        Date currentDate = timeSource.currentTimestamp();

        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy");
        return getMessage("fileNamePrefix")
                + " "
                + riz.getNumber()
                + " "
                + getMessage("fileName")
                + " "
                + dt.format(currentDate)
                + ".xls";
    }

    private Map<String, Object> makeParams(Date dFrom, Date dTo, Riz riz) {
        Map<String, Object> params = new HashMap<>();
        params.put("date_from", dFrom);
        params.put("date_to", dTo);
        params.put("riz", riz.getId());

        Date currentDate = timeSource.currentTimestamp();

        params.put("report_date", currentDate);
        RizContract rc = riz.getContract().iterator().next();
        params.put("contract_num", rc.getContractNumber());
        params.put("contract_date", rc.getContractDate());
        params.put("key_amount", 0);
        return params;
    }

    private Boolean validateFields(Date dFrom, Date dTo, Riz riz) {

        if (dFrom == null) {
            showMessageDialog(
                    getMessage("Result"),
                    getMessage("sDateF"),
                    MessageType.WARNING);
            return false;
        } else if (dTo == null) {
            showMessageDialog(
                    getMessage("Result"),
                    getMessage("sDateT"),
                    MessageType.WARNING);
            return false;
        } else if (riz == null) {
            showMessageDialog(
                    getMessage("Result"),
                    getMessage("sR"),
                    MessageType.WARNING);
            return false;
        } else if (dFrom.getTime() > dTo.getTime()) {
            showMessageDialog(
                    getMessage("Result"),
                    getMessage("dates"),
                    MessageType.WARNING);
        } else
            return true;

        return false;
    }


    @Inject
    protected ReportService reportService;


    @Inject
    protected EmailService emailService;


    public void onBMakeAndSendClick() {
        showMessageDialog(
                getMessage("Result"),
                getMessage("notCompleted"),
                MessageType.WARNING);
        /*
        Date dFrom = dfFrom.getValue();
        Date dTo = dfTo.getValue();

        Calendar cal = Calendar.getInstance();
        cal.setTime(dTo);
        cal.add(Calendar.HOUR, +23);
        cal.add(Calendar.MINUTE, +59);
        cal.add(Calendar.SECOND, +59);
        dTo = cal.getTime();

        Riz riz = lfRiz.getValue();

        if (validateFields(dFrom, dTo, riz)) {
            Map<String, Object> params = makeParams(dFrom, dTo, riz);
            String outputFileName = getFileName(riz);
            List<Report> reportCollection = reportGuiManager.getAvailableReports(
                    null,
                    userSessionSource.getUserSession().getUser(),
                    null
            );
            if (reportCollection.size() > 0) {
                for (Report report : reportCollection) {
                    if (report.getName().equals("regkey_act")) {
                        FileDescriptor reportFile = reportService.createAndSaveReport(report,params,outputFileName);

                        *//*TODO attach report to email and send*//*

                    }
                }
            }
        }
*/

    }
}