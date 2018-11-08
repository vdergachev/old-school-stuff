/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.crudentity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import java.util.Date;
import ru.glavkniga.gklients.entity.OnetimeMailing;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;

/**
 * @author LysovIA
 */
@MetaClass(name = "gklients$DailyNewsDistribution")
public class DailyNewsDistribution extends AbstractNotPersistentEntity {
    private static final long serialVersionUID = 8556267417314683411L;

    @MetaProperty
    protected String distributionTitle;

    @MetaProperty
    protected Date distributionDate;

    @MetaProperty
    protected String introText;

    @MetaProperty
    protected OnetimeMailing onetimeMailing;

    public void setDistributionTitle(String distributionTitle) {
        this.distributionTitle = distributionTitle;
    }

    public String getDistributionTitle() {
        return distributionTitle;
    }


    public void setDistributionDate(Date distributionDate) {
        this.distributionDate = distributionDate;
    }

    public Date getDistributionDate() {
        return distributionDate;
    }

    public void setIntroText(String introText) {
        this.introText = introText;
    }

    public String getIntroText() {
        return introText;
    }

    public void setOnetimeMailing(OnetimeMailing onetimeMailing) {
        this.onetimeMailing = onetimeMailing;
    }

    public OnetimeMailing getOnetimeMailing() {
        return onetimeMailing;
    }


}