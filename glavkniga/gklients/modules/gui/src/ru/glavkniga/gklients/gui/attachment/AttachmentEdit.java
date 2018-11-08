/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.attachment;

import com.haulmont.cuba.gui.components.AbstractEditor;
import ru.glavkniga.gklients.entity.Attachment;
import ru.glavkniga.gklients.enumeration.AttachmentMethod;
import ru.glavkniga.gklients.service.SiteFileUploadService;

import javax.inject.Inject;
import java.net.URL;

/**
 * @author IgorLysov
 */
public class AttachmentEdit extends AbstractEditor<Attachment> {

    @Inject
    private SiteFileUploadService uploadService;

    @Override
    protected boolean preCommit() {
        Attachment item = getItem();
        if (item.getAttachmentMethod() == AttachmentMethod.Separate && (item.getUrl() == null || item.getUrl().isEmpty())) {
            URL url = uploadService.uploadAttachment(item.getFile());
            if (url != null){
            item.setUrl(url.toString());}
            else {
                showNotification("unable to upload file to the website");
                return false;
            }
        }
        return true;
    }
}