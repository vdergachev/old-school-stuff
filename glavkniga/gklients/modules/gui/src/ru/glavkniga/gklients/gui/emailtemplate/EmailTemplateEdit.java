/*
 * Copyright (c) 2015 ru.glavkniga.gklients.gui.emailtemplate
 */
package ru.glavkniga.gklients.gui.emailtemplate;

import com.haulmont.cuba.gui.components.AbstractEditor;
import ru.glavkniga.gklients.entity.EmailTemplate;
import ru.glavkniga.gklients.enumeration.SysEmail;

/**
 * @author LysovIA
 */
public class EmailTemplateEdit extends AbstractEditor<EmailTemplate> {

    @Override
    protected boolean preCommit() {

        if (getItem().getEmailType() == SysEmail.ONETIME_MAILING) {
            String templateBody = getItem().getBody();

            if (templateBody == null || !templateBody.contains("[CONTENT]")) {
                showMessageDialog(getMessage("cantSaveMsg"), getMessage("templateWrongMsg"), MessageType.WARNING);
                return false;
            }
        }
        return super.preCommit();
    }
}