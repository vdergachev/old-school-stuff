/*
 * Copyright (c) 2015 gklients
 */

package ru.glavkniga.gklients.gui.elversubscription;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.EntityLogAttr;
import com.haulmont.cuba.security.entity.EntityLogItem;
import ru.glavkniga.gklients.entity.ElverSubscription;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by LysovIA on 25.11.2015.
 */
public class CustomColumnGenerator implements Table.PrintableColumnGenerator<ElverSubscription, String> {

    private ComponentsFactory componentsFactory;

    private String fieldName;
    private String methodName;
    private String message;
    private Collection<EntityLogItem> logItems;

    public CustomColumnGenerator(Collection<EntityLogItem> logItems,
                                 String fieldName,
                                 ComponentsFactory componentsFactory,
                                 String nullMessage) {
        this.fieldName = fieldName;
        this.methodName = "get" + firstUpperCase(fieldName);
        this.componentsFactory = componentsFactory;
        this.logItems = logItems;
        this.message = nullMessage;
    }

    private String firstUpperCase(String word) {
        if (word == null || word.isEmpty())
            return word;
        return
                word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private String makeDescription(ElverSubscription item) {
        String value = null;
        if (logItems.size() > 0) {
            Iterator it = logItems.iterator();
            do {
                EntityLogItem logItem = (EntityLogItem) it.next();
                if (logItem.getEntityRef().getEntityId().equals(item.getId())) {
                    Set<EntityLogAttr> logItemAttrs = logItem.getAttributes();
                    for (EntityLogAttr logItemAttr : logItemAttrs) {
                        if (logItemAttr.getName().equals(fieldName)) {
                            value = logItem.getUser().getName() + " ";
                            value += new SimpleDateFormat("dd.MM.yyy HH:mm:ss")
                                    .format(logItem.getEventTs());
                        }
                    }
                }
            } while (value == null && it.hasNext());
        }
        if (value == null)
            value = message;
        return value;
    }

    @Override
    public Component generateCell(ElverSubscription item) {
        Label label = componentsFactory.createComponent(Label.class);
        Boolean boolVal = getBoolValue(item);

        label.setValue(boolVal);
        if (boolVal) {
            label.setStyleName("cell-true");
        }else{
            label.setStyleName("cell-false");
        }

        label.setDescription(makeDescription(item));
        return label;
    }

    @Override
    public String getValue(ElverSubscription item) {
        Boolean boolVal = getBoolValue(item);
        return boolVal.toString();
    }

    private boolean getBoolValue(ElverSubscription item) {
        boolean boolVal = Boolean.FALSE;
        try {
            Method classMethod = ElverSubscription.class.getMethod(methodName);
            if (classMethod.invoke(item) == null)
                boolVal = false;
            else
                boolVal = (boolean) classMethod.invoke(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return boolVal;
    }
}
