/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.screens;

import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import ru.glavkniga.gklients.service.DateTimeService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Igor Lysov on 13.06.2017.
 */

public class RizByPeriodReportExecutor extends AbstractWindow {

    @Named("dfFrom")
    DateField dfFrom;

    @Named("dfTo")
    DateField dfTo;

    @Inject
    private ReportGuiManager reportGuiManager;

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private DateTimeService dts;


    @Override
    public void init(Map<String, Object> params) {
        dfFrom.setValue(dts.getStartOfToday());
        dfTo.setValue(dts.getEndOfToday());
    }

    public void onBokClick() {
        Date dFrom = dts.getStartOfDay(dfFrom.getValue());
        Date dTo = dts.getEndOfDay(dfTo.getValue());

        if (validateFields(dFrom, dTo)) {
            Map<String, Object> params = makeParams(dFrom, dTo);
            String outputFileName = "RizReport" + (new Date().toString()) + ".xls";

            List<Report> reportCollection = reportGuiManager.getAvailableReports(
                    null,
                    userSessionSource.getUserSession().getUser(),
                    null
            );
            if (reportCollection.size() > 0) {
                for (Report report : reportCollection) {
                    String reportCode = report.getCode();
                    if (reportCode != null && reportCode.equals("rizByPeriod")) {
                        reportGuiManager.printReport(report,
                                params,
                                null,
                                outputFileName,
                                null);
                    }
                }
            }
        }
        onBcancelClick();
    }

    private Boolean validateFields(Date dFrom, Date dTo) {

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
        } else if (dFrom.getTime() > dTo.getTime()) {
            showMessageDialog(
                    getMessage("Result"),
                    getMessage("dates"),
                    MessageType.WARNING);
        } else
            return true;

        return false;
    }

    private Map<String, Object> makeParams(Date dFrom, Date dTo) {
        Map<String, Object> params = new HashMap<>();
        params.put("date_from", dFrom);
        params.put("date_to", dTo);
        return params;
    }

    public void onBcancelClick() {
        this.close(Window.CLOSE_ACTION_ID);
    }

}