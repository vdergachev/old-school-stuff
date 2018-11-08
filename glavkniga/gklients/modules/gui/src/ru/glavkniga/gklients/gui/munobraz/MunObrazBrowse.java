/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.gui.munobraz;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import ru.glavkniga.gklients.entity.MunObraz;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import ru.glavkniga.gklients.service.CsvParserService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static com.haulmont.cuba.gui.components.FileUploadField.*;

/**
 * @author LysovIA
 */
public class MunObrazBrowse extends AbstractLookup {

    @Inject
    private CollectionDatasource<MunObraz, UUID> munObrazesDs;

    @Inject
    private Datasource<MunObraz> munObrazDs;

    @Inject
    private Table<MunObraz> munObrazesTable;

    @Inject
    private BoxLayout lookupBox;

    @Inject
    private BoxLayout actionsPane;

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private CsvParserService csvService;

    @Named("munObrazesTable.remove")
    private RemoveAction munObrazesTableRemove;


    private boolean creating;


    @Inject
    protected FileUploadField uploadField;

    @Inject
    protected FileUploadingAPI fileUploading;

    @Inject
    protected DataSupplier dataSupplier;



    @Override
    public void init(Map<String, Object> params) {
        munObrazesDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                MunObraz reloadedItem = dataSupplier.reload(e.getDs().getItem(), munObrazDs.getView());
                munObrazDs.setItem(reloadedItem);
            }
        });

        munObrazesTable.addAction(new CreateAction(munObrazesTable) {
            @Override
            protected void internalOpenEditor(CollectionDatasource datasource, Entity newItem, Datasource parentDs, Map<String, Object> params) {
                munObrazesTable.setSelected(Collections.emptyList());
                munObrazDs.setItem((MunObraz) newItem);
                enableEditControls(true);
            }
        });

        munObrazesTable.addAction(new EditAction(munObrazesTable) {
            @Override
            protected void internalOpenEditor(CollectionDatasource datasource, Entity existingItem, Datasource parentDs, Map<String, Object> params) {
                if (munObrazesTable.getSelected().size() == 1) {
                    enableEditControls(false);
                }
            }
        });

        munObrazesTableRemove.setAfterRemoveHandler(removedItems -> munObrazDs.setItem(null));

        disableEditControls();

        uploadField.addFileUploadSucceedListener(fileEvent -> {
            FileDescriptor fd = uploadField.getFileDescriptor();
            try {
                // save file to FileStorage
                fileUploading.putFileIntoStorage(uploadField.getFileId(), fd);
            } catch (FileStorageException e) {
                throw new RuntimeException(e);
            }
            // save file descriptor to database
            dataSupplier.commit(fd);

            csvService.parseCsvToMunObraz(fd);
            munObrazesTable.refresh();
            showNotification("File uploaded: " + uploadField.getFileName(), NotificationType.HUMANIZED);
        });

    }

    public void save() {
        getDsContext().commit();

        MunObraz editedItem = munObrazDs.getItem();
        if (creating) {
            munObrazesDs.includeItem(editedItem);
        } else {
            munObrazesDs.updateItem(editedItem);
        }
        munObrazesTable.setSelected(editedItem);

        disableEditControls();
    }

    public void cancel() {
        MunObraz selectedItem = munObrazesDs.getItem();
        if (selectedItem != null) {
            MunObraz reloadedItem = dataSupplier.reload(selectedItem, munObrazDs.getView());
            munObrazesDs.setItem(reloadedItem);
        } else {
            munObrazDs.setItem(null);
        }

        disableEditControls();
    }

    private void enableEditControls(boolean creating) {
        this.creating = creating;
        initEditComponents(true);
        fieldGroup.requestFocus();
    }

    private void disableEditControls() {
        initEditComponents(false);
        munObrazesTable.requestFocus();
    }

    private void initEditComponents(boolean enabled) {
        fieldGroup.setEditable(enabled);
        actionsPane.setVisible(enabled);
        lookupBox.setEnabled(!enabled);
    }

    public void btnParseClick(Component source) {
    }
}