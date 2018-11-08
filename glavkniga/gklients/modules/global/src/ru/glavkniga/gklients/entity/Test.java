/*
 * Copyright (c) 2016 gklients
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author LysovIA
 */
@NamePattern("%s|testName")
@Table(name = "GKLIENTS_TEST")
@Entity(name = "gklients$Test")
public class Test extends StandardEntity {
    private static final long serialVersionUID = -554414245708218707L;

    @Column(name = "TEST_ID")
    protected Integer testId;

    @Column(name = "TEST_NAME")
    protected String testName;

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestName() {
        return testName;
    }


}