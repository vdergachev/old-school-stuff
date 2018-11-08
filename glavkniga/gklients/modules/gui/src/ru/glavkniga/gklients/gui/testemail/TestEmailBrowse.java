/*
 * Copyright (c) 2016 gklients
 */
package ru.glavkniga.gklients.gui.testemail;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import ru.glavkniga.gklients.entity.Test;
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
public class TestEmailBrowse extends AbstractLookup {

    @Named("testsDs")
    private CollectionDatasource<Test, UUID> testsDs;

    @Named("testMarksFullDs")
    CollectionDatasource<TestMark, UUID> testMarksFullDs;


    @Named("testEmailsDs")
    CollectionDatasource<TestEmail, UUID> testEmailsDs;

    @Inject
    protected GKExchangeService gkExchangeService;

    public void onRefreshBtnClick(Component source) {
        int testsAdded = getTests();
        int testEmailsAdded = getTestEmails();
        int testMarksAdded = getMarks();

        showMessageDialog("count", "Result:\r\n" +
                        testsAdded + " tests added\r\n" +
                        testEmailsAdded + " test emails added\r\n" +
                        testMarksAdded + " test marks added"
                , MessageType.WARNING);
    }

    private int getTestEmails() {
        testEmailsDs.refresh();
        Map<String, Object> response = gkExchangeService.getTestEmail();
        Collection<Object> responseColletion = response.values();
        Collection<TestEmail> testCollection = testEmailsDs.getItems();
        Iterator<Object> iter = responseColletion.iterator();
        int added = 0;

        boolean flag = true;
        while (iter.hasNext()) {
            TestEmail responseTest = (TestEmail) iter.next();
            Iterator<TestEmail> iterTest = testCollection.iterator();
            while (iterTest.hasNext()) {
                TestEmail testEmail = iterTest.next();
                if (testEmail.getEmailIndex().equals(responseTest.getEmailIndex()))
                //TODO there is a NPE if Index is empty in this case. Get rid of it here and in the other places
                {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                testEmailsDs.addItem(responseTest);
                added++;
            }
            flag = true;
        }
        testEmailsDs.commit();
        return added;
    }

    public int getTests() {
        testsDs.refresh();
        Map<String, Object> response = gkExchangeService.getTest();
        Collection<Object> responseColletion = response.values();
        Collection<Test> testCollection = testsDs.getItems();
        Iterator<Object> iter = responseColletion.iterator();
        int added = 0;

        boolean flag = true;
        while (iter.hasNext()) {
            Test responseTest = (Test) iter.next();
            Iterator<Test> iterTest = testCollection.iterator();
            while (iterTest.hasNext()) {
                Test test = iterTest.next();
                if (test.getTestId() == responseTest.getTestId()) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                testsDs.addItem(responseTest);
                added++;
            }
            flag = true;
        }
        testsDs.commit();

        return added;
    }

    public int getMarks() {
        testMarksFullDs.refresh();
        Map<String, Object> response = gkExchangeService.getTestMark();
        Collection<Object> responseColletion = response.values();
        Collection<TestMark> marksCollection = testMarksFullDs.getItems();
        Iterator<Object> iter = responseColletion.iterator();
        int added = 0;

        boolean flag = true;
        while (iter.hasNext()) {
            TestMark responseMark = (TestMark) iter.next();
            Iterator<TestMark> iterMark = marksCollection.iterator();
            while (iterMark.hasNext()) {
                TestMark testMark = iterMark.next();
                if (testMark.getMarkIndex().equals(responseMark.getMarkIndex())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                testsDs.refresh();
                Collection<Test> testCollection = testsDs.getItems();
                Iterator<Test> iterTest = testCollection.iterator();
                while (iterTest.hasNext()) {
                    Test test = iterTest.next();
                    if (test.getTestId() == responseMark.getTestIndex()) {
                        responseMark.setTest(test);
                        break;
                    }
                }
                testEmailsDs.refresh();
                Collection<TestEmail> emailCollection = testEmailsDs.getItems();
                Iterator<TestEmail> iterEmail = emailCollection.iterator();
                while (iterEmail.hasNext()) {
                    TestEmail testEmail = iterEmail.next();
                    if (testEmail.getEmailIndex().equals(responseMark.getEmailIndex()))
                    {
                        responseMark.setEmail(testEmail);
                        break;
                    }
                }
                testMarksFullDs.addItem(responseMark);
                added++;
            }
            flag = true;
        }
        testMarksFullDs.commit();
        return added;
    }


    public void onBtnEraseClick(Component source) {
    }
}