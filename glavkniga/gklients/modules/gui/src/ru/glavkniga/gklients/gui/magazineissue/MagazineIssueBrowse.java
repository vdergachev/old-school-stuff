/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.gui.magazineissue;

import ru.glavkniga.gklients.entity.MagazineIssue;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * @author LysovIA
 */
public class MagazineIssueBrowse extends AbstractLookup {

    @Inject
    private CollectionDatasource<MagazineIssue, UUID> magazineIssuesDs;

    @Inject
    private Datasource<MagazineIssue> magazineIssueDs;

    @Inject
    private Table<MagazineIssue> magazineIssuesTable;

    @Inject
    private BoxLayout lookupBox;

    @Inject
    private BoxLayout actionsPane;

    @Inject
    private FieldGroup fieldGroup;

    

    @Named("magazineIssuesTable.remove")
    private RemoveAction magazineIssuesTableRemove;

    @Inject
    private DataSupplier dataSupplier;

    private boolean creating;

    @Override
    public void init(Map<String, Object> params) {
        magazineIssuesDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                MagazineIssue reloadedItem = dataSupplier.reload(e.getDs().getItem(), magazineIssueDs.getView());
                magazineIssueDs.setItem(reloadedItem);
            }
        });

        magazineIssuesTable.addAction(new CreateAction(magazineIssuesTable) {
            @Override
            protected void internalOpenEditor(CollectionDatasource datasource, Entity newItem, Datasource parentDs, Map<String, Object> params) {
                magazineIssuesTable.setSelected(Collections.emptyList());
                magazineIssueDs.setItem((MagazineIssue) newItem);
                enableEditControls(true);
            }
        });

        magazineIssuesTable.addAction(new EditAction(magazineIssuesTable) {
            @Override
            protected void internalOpenEditor(CollectionDatasource datasource, Entity existingItem, Datasource parentDs, Map<String, Object> params) {
                if (magazineIssuesTable.getSelected().size() == 1) {
                    enableEditControls(false);
                }
            }
        });

        magazineIssuesTableRemove.setAfterRemoveHandler(removedItems -> magazineIssueDs.setItem(null));

        disableEditControls();
    }

    public void save() {
        getDsContext().commit();

        MagazineIssue editedItem = magazineIssueDs.getItem();
        if (creating) {
            magazineIssuesDs.includeItem(editedItem);
        } else {
            magazineIssuesDs.updateItem(editedItem);
        }
        magazineIssuesTable.setSelected(editedItem);

        disableEditControls();
    }

    public void cancel() {
        MagazineIssue selectedItem = magazineIssuesDs.getItem();
        if (selectedItem != null) {
            MagazineIssue reloadedItem = dataSupplier.reload(selectedItem, magazineIssueDs.getView());
            magazineIssuesDs.setItem(reloadedItem);
        } else {
            magazineIssueDs.setItem(null);
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
        magazineIssuesTable.requestFocus();
    }

    private void initEditComponents(boolean enabled) {
        fieldGroup.setEditable(enabled);
        actionsPane.setVisible(enabled);
        lookupBox.setEnabled(!enabled);
    }
}