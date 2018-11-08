package ru.glavkniga.gklients.crud;

import com.haulmont.cuba.core.entity.Entity;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.annotation.PostConstruct;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class GkIntegrationTypeHelper {

    private final static String MAPPING_FILE_PATH = "/ru/glavkniga/gklients/crud/database-mapping.xml";

    private final Map<String, String> classToTable = new HashMap<>();
    private final Map<String, String> tableToClass = new HashMap<>();
    private final Map<String, Map<String, String>> classColumnToAttribute = new HashMap<>();
    private final Map<String, Map<String, String>> tableAttributeToColumn = new HashMap<>();
    private final Map<Class<? extends Entity>, Map<String, Class>> entityAttributeTypeCache = new HashMap<>();

    @PostConstruct
    public void init() {
        loadMappingFromFile();
    }

    public Class<? extends Entity> getEntityClass(final String tableName) {
        if (!tableToClass.containsKey(tableName)) {
            return null;
        }
        try {
            final Class clazz = Class.forName(tableToClass.get(tableName));
            if (!Entity.class.isAssignableFrom(clazz)) {
                return null;
            }
            final Class<? extends Entity> entityClass = (Class<? extends Entity>) clazz;
            addEntityAttributesToCache(entityClass);
            return entityClass;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void addEntityAttributesToCache(final Class<? extends Entity> clazz) {
        if(entityAttributeTypeCache.containsKey(clazz)){
            return;
        }
        final Map<String, Class> entityFieldsType = new HashMap<>();
        ReflectionUtils.doWithFields(clazz,
                field -> entityFieldsType.put(field.getName(), field.getType()),
                field -> !field.getName().startsWith("_"));
        entityAttributeTypeCache.put(clazz, entityFieldsType);
    }

    public Class getEntityAttributeType(final Class<? extends Entity> clazz, final String attribute){
        if(!entityAttributeTypeCache.containsKey(clazz)){
            return null;
        }
        final Map<String, Class> entityFieldsType = entityAttributeTypeCache.get(clazz);
        return entityFieldsType.get(attribute);
    }

    public String getProperty(final String tableName, final String columnName) {
        final String className = tableToClass.get(tableName);

        if (!classColumnToAttribute.containsKey(className)) {
            return null;
        }
        final Map<String, String> columnToAttribute = classColumnToAttribute.get(className);
        if (columnToAttribute == null || !columnToAttribute.containsKey(columnName)) {
            return null;
        }
        return columnToAttribute.get(columnName);
    }

    private void loadMappingFromFile() {

        try (final InputStream stream = this.getClass().getResourceAsStream(MAPPING_FILE_PATH)) {
            SAXParserFactory.newInstance().newSAXParser()
                    .parse(stream, new GkMappingHandler(tableToClass, classToTable, classColumnToAttribute,
                            tableAttributeToColumn));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static class GkMappingHandler extends DefaultHandler {

        private String className;
        private String tableName;

        private Map<String, String> attributeToColumn = new HashMap<>();
        private Map<String, String> columnToAttribute = new HashMap<>();

        private final Map<String, String> classToTable;
        private final Map<String, String> tableToClass;
        private final Map<String, Map<String, String>> classColumnToAttribute;
        private final Map<String, Map<String, String>> tableAttributeToColumn;


        private GkMappingHandler(final Map<String, String> tableToClass, final Map<String, String> classToTable,
                                 Map<String, Map<String, String>> classColumnToAttribute,
                                 Map<String, Map<String, String>> tableAttributeToColumn) {

            this.classToTable = classToTable;
            this.tableToClass = tableToClass;
            this.classColumnToAttribute = classColumnToAttribute;
            this.tableAttributeToColumn = tableAttributeToColumn;
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {

            switch (qName.toLowerCase()) {
                case "entity":
                    className = attributes.getValue("class");
                    break;
                case "property":
                    final String attributeName = attributes.getValue("name");
                    final String columnName = attributes.getValue("field");
                    columnToAttribute.put(columnName, attributeName);
                    attributeToColumn.put(attributeName, columnName);
                    break;
                case "table":
                    tableName = attributes.getValue("name");
                    tableToClass.put(tableName, className);
                    classToTable.put(className, tableName);
                    break;
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (qName.toLowerCase()) {
                case "table":
                    merge(classColumnToAttribute, className, columnToAttribute);
                    merge(tableAttributeToColumn, tableName, attributeToColumn);
                    columnToAttribute = new HashMap<>();
                    attributeToColumn = new HashMap<>();
                    break;
            }
        }

        private void merge(final Map<String, Map<String, String>> map, final String key,
                           final Map<String, String> value) {
            if (map.containsKey(key)) {
                map.get(key).putAll(value);
            } else {
                map.put(key, value);
            }
        }
    }

//    public static void main(String[] args) {
//        new GkIntegrationTypeHelper().init();
//    }
}
