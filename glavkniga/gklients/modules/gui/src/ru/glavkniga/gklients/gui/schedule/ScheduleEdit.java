/*
 * Copyright (c) 2015 ru.glavkniga.gklients.gui.schedule
 */
package ru.glavkniga.gklients.gui.schedule;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import ru.glavkniga.gklients.entity.Magazine;
import ru.glavkniga.gklients.entity.MagazineIssue;
import ru.glavkniga.gklients.entity.Schedule;

import javax.inject.Named;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LysovIA
 */
public class ScheduleEdit extends AbstractEditor<Schedule> {

    @Named("magazineDs")
    CollectionDatasource<Magazine, UUID> magazineDs;

    @Override
    public void init(Map<String, Object> params) {
        getDialogOptions().setWidth(650);
    }

    @Override
    protected void initNewItem(Schedule item) {
        MagazineIssue magazineIssue = new MagazineIssue();
        item.setMagazineIssue(magazineIssue);
    }

    @Override
    protected boolean preCommit() {
        MagazineIssue magazineIssue = getItem().getMagazineIssue();
        String code = magazineIssue.getCode();
        String magazineAbb = code.substring(0, 2);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("abb", magazineAbb);
        magazineDs.refresh(parameters);
        if (magazineDs.size() > 0) {
            magazineIssue.setMagazine(magazineDs.getItems().iterator().next());
        } else {
            showMessageDialog(
                    getMessage("warningCap"),
                    getMessage("wrongAbbMsg"),
                    MessageType.WARNING
            );
            return false;
        }

        try {
            int year = Integer.parseInt(code.substring(3, 7));
            int number = Integer.parseInt(code.substring(8, 10));
            int month = (number + 1) / 2;
            magazineIssue.setNumber(code.substring(8, code.length()));
            magazineIssue.setYear(year + "");
            magazineIssue.setMonth(month + "");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.YEAR, year);
            if (number % 2 == 1) {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
            } else {
                calendar.set(Calendar.DAY_OF_MONTH, 15);
            }
            magazineIssue.setIssuePlannedDate(calendar.getTime());
        } catch (NullPointerException e) {
            e.printStackTrace();
            showMessageDialog(
                    getMessage("warningCap"),
                    getMessage("internalErrorMsg"),
                    MessageType.WARNING
            );
            return false;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showMessageDialog(
                    getMessage("warningCap"),
                    getMessage("wrongCodeMsg"),
                    MessageType.WARNING
            );
            return false;
        }
        return true;
    }

}