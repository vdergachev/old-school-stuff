/*
 * Copyright (c) 2016 gklients
 */
package ru.glavkniga.gklients.gui.testmark;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import ru.glavkniga.gklients.entity.TestEmail;
import ru.glavkniga.gklients.entity.TestMark;
import ru.glavkniga.gklients.service.GKExchangeService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * @author LysovIA
 */
public class TestMarkBrowse extends AbstractLookup {


    @Named("testMarksDs")
    CollectionDatasource<TestMark, UUID> testMarksDs;

    @Inject
    protected GKExchangeService gkExchangeService;

    public void onRefreshBtnClick(Component source) {
        Map<String, Object> response = gkExchangeService.getTestMark();
        Collection<Object> responseColletion = response.values();
        Collection<TestMark> testCollection = testMarksDs.getItems();
        Iterator<Object> iter = responseColletion.iterator();
        int added = 0;

        boolean flag = true;
        while (iter.hasNext()) {
            TestMark responseTest = (TestMark) iter.next();
            Iterator<TestMark> iterTest = testCollection.iterator();
            while (iterTest.hasNext()) {
                TestMark testMark = iterTest.next();
                if (testMark.getMarkIndex ().equals(responseTest.getMarkIndex())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                testMarksDs.addItem(responseTest);
                added++;
            }
            flag = true;
        }
        testMarksDs.commit();
        testMarksDs.refresh();
        showMessageDialog("countTestMarks", added + " items added", MessageType.WARNING);
    }
}