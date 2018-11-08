/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.gui.token;

import ru.glavkniga.gklients.entity.Token;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.security.entity.EntityOp;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * @author IgorLysov
 */
public class TokenBrowse extends AbstractLookup {

    /**
     * The {@link CollectionDatasource} instance that loads a list of {@link Token} records
     * to be displayed in {@link TokenBrowse#tokensTable} on the left
     */
    @Inject
    private CollectionDatasource<Token, UUID> tokensDs;

    /**
     * The {@link Datasource} instance that contains an instance of the selected entity
     * in {@link TokenBrowse#tokensDs}
     * <p/> Containing instance is loaded in {@link CollectionDatasource#addItemChangeListener}
     * with the view, specified in the XML screen descriptor.
     * The listener is set in the {@link TokenBrowse#init(Map)} method
     */
    @Inject
    private Datasource<Token> tokenDs;

    /**
     * The {@link Table} instance, containing a list of {@link Token} records,
     * loaded via {@link TokenBrowse#tokensDs}
     */
    @Inject
    private Table<Token> tokensTable;

    /**
     * The {@link BoxLayout} instance that contains components on the left side
     * of {@link SplitPanel}
     */
    @Inject
    private BoxLayout lookupBox;

    /**
     * The {@link BoxLayout} instance that contains buttons to invoke Save or Cancel actions in edit mode
     */
    @Inject
    private BoxLayout actionsPane;

    /**
     * The {@link FieldGroup} instance that is linked to {@link TokenBrowse#tokenDs}
     * and shows fields of the selected {@link Token} record
     */
    @Inject
    private FieldGroup fieldGroup;

    /**
     * The {@link RemoveAction} instance, related to {@link TokenBrowse#tokensTable}
     */
    @Named("tokensTable.remove")
    private RemoveAction tokensTableRemove;

    @Inject
    private DataSupplier dataSupplier;

    /**
     * {@link Boolean} value, indicating if a new instance of {@link Token} is being created
     */
    private boolean creating;

    @Override
    public void init(Map<String, Object> params) {

        /*
         * Adding {@link com.haulmont.cuba.gui.data.Datasource.ItemChangeListener} to {@link tokensDs}
         * The listener reloads the selected record with the specified view and sets it to {@link tokenDs}
         */
        tokensDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                Token reloadedItem = dataSupplier.reload(e.getDs().getItem(), tokenDs.getView());
                tokenDs.setItem(reloadedItem);
            }
        });

        /*
         * Adding {@link CreateAction} to {@link tokensTable}
         * The listener removes selection in {@link tokensTable}, sets a newly created item to {@link tokenDs}
         * and enables controls for record editing
         */
        tokensTable.addAction(new CreateAction(tokensTable) {
            @Override
            protected void internalOpenEditor(CollectionDatasource datasource, Entity newItem, Datasource parentDs, Map<String, Object> params) {
                tokensTable.setSelected(Collections.emptyList());
                tokenDs.setItem((Token) newItem);
                refreshOptionsForLookupFields();
                enableEditControls(true);
            }
        });

        /*
         * Adding {@link EditAction} to {@link tokensTable}
         * The listener enables controls for record editing
         */
        tokensTable.addAction(new EditAction(tokensTable) {
            @Override
            protected void internalOpenEditor(CollectionDatasource datasource, Entity existingItem, Datasource parentDs, Map<String, Object> params) {
                if (tokensTable.getSelected().size() == 1) {
                    refreshOptionsForLookupFields();
                    enableEditControls(false);
                }
            }

            @Override
            public void refreshState() {
                if (target != null) {
                    CollectionDatasource ds = target.getDatasource();
                    if (ds != null && !captionInitialized) {
                        setCaption(messages.getMainMessage("actions.Edit"));
                    }
                }
                super.refreshState();
            }

            @Override
            protected boolean isPermitted() {
                CollectionDatasource ownerDatasource = target.getDatasource();
                boolean entityOpPermitted = security.isEntityOpPermitted(ownerDatasource.getMetaClass(), EntityOp.UPDATE);
                if (!entityOpPermitted) {
                    return false;
                }
                return super.isPermitted();
            }
        });

        /*
         * Setting {@link RemoveAction#afterRemoveHandler} for {@link tokensTableRemove}
         * to reset record, contained in {@link tokenDs}
         */
        tokensTableRemove.setAfterRemoveHandler(removedItems -> tokenDs.setItem(null));

        disableEditControls();
    }

    private void refreshOptionsForLookupFields() {
        for (Component component : fieldGroup.getOwnComponents()) {
            if (component instanceof LookupField) {
                CollectionDatasource optionsDatasource = ((LookupField) component).getOptionsDatasource();
                if (optionsDatasource != null) {
                    optionsDatasource.refresh();
                }
            }
        }
    }

    /**
     * Method that is invoked by clicking Ok button after editing an existing or creating a new record
     */
    public void save() {
        if (!validate(Collections.singletonList(fieldGroup))) {
            return;
        }
        getDsContext().commit();

        Token editedItem = tokenDs.getItem();
        if (creating) {
            tokensDs.includeItem(editedItem);
        } else {
            tokensDs.updateItem(editedItem);
        }
        tokensTable.setSelected(editedItem);

        disableEditControls();
    }

    /**
     * Method that is invoked by clicking Cancel button, discards changes and disables controls for record editing
     */
    public void cancel() {
        Token selectedItem = tokensDs.getItem();
        if (selectedItem != null) {
            Token reloadedItem = dataSupplier.reload(selectedItem, tokenDs.getView());
            tokensDs.setItem(reloadedItem);
        } else {
            tokenDs.setItem(null);
        }

        disableEditControls();
    }

    /**
     * Enabling controls for record editing
     * @param creating indicates if a new instance of {@link Token} is being created
     */
    private void enableEditControls(boolean creating) {
        this.creating = creating;
        initEditComponents(true);
        fieldGroup.requestFocus();
    }

    /**
     * Disabling editing controls
     */
    private void disableEditControls() {
        initEditComponents(false);
        tokensTable.requestFocus();
    }

    /**
     * Initiating edit controls, depending on if they should be enabled/disabled
     * @param enabled if true - enables editing controls and disables controls on the left side of the splitter
     *                if false - visa versa
     */
    private void initEditComponents(boolean enabled) {
        fieldGroup.setEditable(enabled);
        actionsPane.setVisible(enabled);
        lookupBox.setEnabled(!enabled);
    }
}