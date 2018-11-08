/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crud;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Resources;
import org.apache.commons.compress.utils.IOUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by LysovIA on 10.02.2017.
 */
public class Mapper {

    public static final String MAPPING_PATH = "/ru/glavkniga/gklients/crud/database-mapping.xml";

    private Resources resources = AppBeans.get(Resources.NAME);
    private String entityName;
    private Map<String, Map<String,String>> tableMap;
    //key = property name, value = name of the field in table
    private Map<String, String> fieldMap;



    /*
    * returns prepared Map of tables and property mappings
    * */
    public Map<String, Map<String,String>> getTableMap() {
        return tableMap;
    }

    private String tableName;


    private boolean fillMap = false;

    /*
    * Prepares map of tables list and fields mapping of each table for given Entity type
    * */

    public Mapper(Class<? extends Entity> entity) {

        this.entityName = entity.getName();
        this.fieldMap = new HashMap<>();
        this.tableMap = new HashMap<>();

        SAXParserFactory factory = SAXParserFactory.newInstance();

        DefaultHandler handler = new DefaultHandler() {

            public void startElement(String uri, String localName, String qName,
                                     Attributes attributes) throws SAXException {
                switch (qName) {
                    case "entity":
                        if (attributes.getValue("class").equals(entityName)) {
                            fillMap = true;
                        }
                        break;
                    case "property":
                        if (fillMap)
                            fieldMap.put(attributes.getValue("name"), attributes.getValue("field"));
                        break;
                    case "table":
                        tableName = attributes.getValue("name");
                        break;
                }
            }

            public void endElement(String uri, String localName,
                                   String qName) throws SAXException {
                switch (qName) {
                    case "table":
                        if (fillMap) {
                            tableMap.put(tableName, fieldMap);
                            fieldMap = new HashMap<>();
                        }
                        break;
                    case "entity":
                        if (fillMap)
                            fillMap = false;
                        break;
                }
            }
        };

        InputStream stream = null;
        try {
            SAXParser saxParser = factory.newSAXParser();
            stream = resources.getResourceAsStream(MAPPING_PATH);
            saxParser.parse(stream, handler);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

}
