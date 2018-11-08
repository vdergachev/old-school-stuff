/*
 * Copyright (c) 2016 gklients
 */
package ru.glavkniga.gklients.gui.test;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import ru.glavkniga.gklients.entity.Test;
import ru.glavkniga.gklients.service.GKExchangeService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * @author LysovIA
 */
public class TestBrowse extends AbstractLookup {

    @Inject
    private CollectionDatasource<Test, UUID> testsDs;

    @Inject
    protected GKExchangeService gkExchangeService;

    public void onBtGetFromWebsiteClick(Component source) {
        Map<String, Object> response = gkExchangeService.getTest();
        Collection<Object> responseColletion = response.values();
        Collection<Test> testCollection = testsDs.getItems();
        Iterator<Object> iter = responseColletion.iterator();
        int added=0;

        boolean flag = true;
        while (iter.hasNext()){
            Test responseTest = (Test) iter.next();
            Iterator<Test> iterTest = testCollection.iterator();
            while (iterTest.hasNext()){
                Test test = iterTest.next();
                if (test.getTestId() == responseTest.getTestId()){
                   flag = false;
                    break;
                }
            }
            if (flag){
                testsDs.addItem(responseTest);
                added++;
            }
            flag = true;
        }
        testsDs.commit();
        testsDs.refresh();
        showMessageDialog("countTest", added+" items added", MessageType.WARNING);
    }
}