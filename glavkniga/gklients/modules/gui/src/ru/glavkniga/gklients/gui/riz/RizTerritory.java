/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.gui.riz;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import ru.glavkniga.gklients.entity.MunObraz;
import ru.glavkniga.gklients.enumeration.Region;
import ru.glavkniga.gklients.entity.Riz;

import javax.inject.Named;
import java.util.*;

/**
 * @author LysovIA
 */
public class RizTerritory extends AbstractEditor {

    @Named("optionsTable")
    protected Table optionsTable;

    @Named("selectedTable")
    protected Table selectedTable;

    @Named("RegionDs")
    protected CollectionDatasource<MunObraz, UUID> regionDs;

    @Named("rizMunObraz")
    CollectionDatasource<MunObraz, UUID> rizMunObraz;

    @Named("rizDs")
    protected Datasource<Riz> rizDs;

    @Named("regEnumField")
    LookupField regEnumField;

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        Region regionSelected = rizDs.getItem().getRegion();
        regEnumField.setValue(regionSelected);
    }

    @Override
    public void init(Map<String, Object> params) {
        //fill up Lookup Field with Enum values
        Map<String, Object> options = new HashMap<>();
        Region[] regionValues = Region.values();
        for (Region region : regionValues) {
            options.put(messages.getMessage(region), region);
        }
        regEnumField.setOptionsMap(sortMap(options));

        //add listener for refresh twincolumn datasource
        regEnumField.addValueChangeListener(e -> {
            Region region = regEnumField.getValue();
            regionDs.refresh(ParamsMap.of("RegionValue", region));
        });
    }

    private SortedMap<String, Object> sortMap(Map<String, Object> source) {
        SortedMap<String, Object> destination;
        destination = new TreeMap<>();
        destination.putAll(source);
        return destination;
    }

    public void onAddSelected() {
            Set selected = optionsTable.getSelected();
            for (Object munObraz : selected) {
                rizMunObraz.addItem((MunObraz) munObraz);
            }

    }

    public void onRemoveSelected() {
        Set selected = selectedTable.getSelected();
        for (Object munObraz : selected) {
            rizMunObraz.removeItem((MunObraz) munObraz);
        }
    }

}