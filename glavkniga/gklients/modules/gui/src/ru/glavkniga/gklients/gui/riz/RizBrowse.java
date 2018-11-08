/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.gui.riz;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import ru.glavkniga.gklients.entity.Riz;
import ru.glavkniga.gklients.service.CsvParserService;
import ru.glavkniga.gklients.service.GKExchangeService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LysovIA
 */
public class RizBrowse extends AbstractLookup {

    @Inject
    private CsvParserService csvService;

    @Named("uploadField")
    protected FileUploadField uploadField;

    @Inject
    protected FileUploadingAPI fileUploading;

    @Inject
    private CollectionDatasource<Riz, UUID> rizesDs;

    @Inject
    private Datasource<Riz> rizDs;

    @Inject
    private Table<Riz> rizesTable;

    @Inject
    private BoxLayout lookupBox;

    @Inject
    private BoxLayout actionsPane;

    @Named("fieldGroup1")
    private FieldGroup fieldGroup1;

    @Named("fieldGroup2_1")
    private FieldGroup fieldGroup2_1;


    @Named("fieldGroup2_2")
    private FieldGroup fieldGroup2_2;

    @Named("fieldGroup2_3")
    private FieldGroup fieldGroup2_3;


    @Named("fieldGroup3_1")
    private FieldGroup fieldGroup3_1;

    @Named("fieldGroup3_2")
    private FieldGroup fieldGroup3_2;

    @Named("fieldGroup3_3")
    private FieldGroup fieldGroup3_3;

    @Named("fieldGroup4")
    private FieldGroup fieldGroup4;




    @Named("terrBtn")
    private Button terrBtn;

    @Named("rizesTable.remove")
    private RemoveAction rizesTableRemove;

    @Inject
    private DataSupplier dataSupplier;

    private boolean creating;

    @Override
    public void init(Map<String, Object> params) {
        rizesDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                Riz reloadedItem = dataSupplier.reload(e.getDs().getItem(), rizDs.getView());
                rizDs.setItem(reloadedItem);
                
                //TODO: make fill local var instead of counter or remove local var at all


                terrBtn.setEnabled(true);
            }
        });

        rizesTable.addAction(new CreateAction(rizesTable) {
            @Override
            protected void internalOpenEditor(CollectionDatasource datasource, Entity newItem, Datasource parentDs, Map<String, Object> params) {
                rizesTable.setSelected(Collections.emptyList());
                rizDs.setItem((Riz) newItem);
                enableEditControls(true);
            }
        });

        rizesTable.addAction(new EditAction(rizesTable) {
            @Override
            protected void internalOpenEditor(CollectionDatasource datasource, Entity existingItem, Datasource parentDs, Map<String, Object> params) {
                if (rizesTable.getSelected().size() == 1) {
                    enableEditControls(false);
                }
            }
        });

        rizesTableRemove.setAfterRemoveHandler(removedItems -> rizDs.setItem(null));
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

            csvService.parseCsvToRiz(fd);
            rizesTable.refresh();
            showNotification("File uploaded: " + uploadField.getFileName(), NotificationType.HUMANIZED);
        });


    }

    public void save() {
        getDsContext().commit();

        Riz editedItem = rizDs.getItem();
        if (creating) {
            rizesDs.includeItem(editedItem);
        } else {
            rizesDs.updateItem(editedItem);
        }
        rizesTable.setSelected(editedItem);

        disableEditControls();
    }

    public void cancel() {
        Riz selectedItem = rizesDs.getItem();
        if (selectedItem != null) {
            Riz reloadedItem = dataSupplier.reload(selectedItem, rizDs.getView());
            rizesDs.setItem(reloadedItem);
        } else {
            rizDs.setItem(null);
        }

        disableEditControls();
    }

    private void enableEditControls(boolean creating) {
        this.creating = creating;
        initEditComponents(true);
        fieldGroup1.requestFocus();
    }

    private void disableEditControls() {
        initEditComponents(false);
        rizesTable.requestFocus();
    }

    private void initEditComponents(boolean enabled) {
        fieldGroup1.setEditable(enabled);
        fieldGroup2_1.setEditable(enabled);
        fieldGroup2_2.setEditable(enabled);
        fieldGroup2_3.setEditable(enabled);
        fieldGroup3_1.setEditable(enabled);
        fieldGroup3_2.setEditable(enabled);
        fieldGroup3_3.setEditable(enabled);
        fieldGroup4.setEditable(enabled);
        actionsPane.setVisible(enabled);
        lookupBox.setEnabled(!enabled);
    }

    public void onTerrbtnClick(Component source) {
        this.openEditor("gklients$RizTerritory", rizesTable.getSingleSelected(), WindowManager.OpenType.DIALOG);
    }

    @Inject
    protected ReportGuiManager reportGuiManager;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected GKExchangeService exchangeService;

    public void onRizExportClick(){
        List<Report> reportCollection = reportGuiManager.getAvailableReports(
                null,
                userSessionSource.getUserSession().getUser(),
                null
        );
        if (reportCollection.size() > 0) {
            for (Report report : reportCollection) {
                if (report.getName().equals("rizWebsiteExport")) {
                    int numRows = exchangeService.setRizData(report);
                    showNotification(numRows +"/"+numRows+ " "+ getMessage("recordsImported"), NotificationType.HUMANIZED);
                }
            }
        }

    }
}