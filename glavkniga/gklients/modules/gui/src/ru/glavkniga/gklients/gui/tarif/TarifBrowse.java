/*
 * Copyright (c) 2015 ru.glavkniga.gklients.gui.tarif
 */
package ru.glavkniga.gklients.gui.tarif;

import com.haulmont.cuba.gui.components.AbstractLookup;

/**
 * @author LysovIA
 */
public class TarifBrowse extends AbstractLookup {
/*
    @Inject
    protected FileUploadField uploadField;

    @Inject
    protected FileUploadingAPI fileUploading;

    @Inject
    protected DataSupplier dataSupplier;

    public void init(Map<String, Object> params) {
        uploadField.addFileUploadSucceedListener(a -> {
            FileDescriptor fd = uploadField.getFileDescriptor();
            try {
                // save file to FileStorage
                fileUploading.putFileIntoStorage(uploadField.getFileId(), fd);
            } catch (FileStorageException e) {
                throw new RuntimeException(e);
            }
            // save file descriptor to database
            FileDescriptor fd_return = dataSupplier.commit(fd);
//            String url = "http://";
//            File file = new File(fd_return.getName())
//            FileParam fileParam = new FileParam()
//
//            HTTPMultipartRequest request = new HTTPMultipartRequest(url,);

            showNotification("File uploaded: " + uploadField.getFileName(), NotificationType.HUMANIZED);
        });
    }*/
}