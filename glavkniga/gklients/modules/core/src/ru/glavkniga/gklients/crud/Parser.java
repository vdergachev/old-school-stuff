/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UuidProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.glavkniga.gklients.crudentity.SiteExchangeEvent;
import ru.glavkniga.gklients.crudentity.SiteExchangeStatus;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by LysovIA on 14.02.2017.
 */
public class Parser {
    //TODO: WHAT IF WE GOT COLLECTION FROM EACH TABLE? Create Parse and ParseCollection methods

    //Store class for which we do parsing
    private Class<? extends Entity> entity;
    //Allows create object of given class
    private Metadata metadata = AppBeans.get(Metadata.NAME);

    //Stores table structure
    private Map<String, Map<String, String>> tables;
    private Map<String, Object> objectMap = new HashMap<>();
    private Logger log = LoggerFactory.getLogger(getClass());

    public Parser(Class<? extends Entity> entity) {
        Mapper mapper = new Mapper(entity);
        this.tables = mapper.getTableMap();
        this.entity = entity;
    }

  /*
   * When an object found - put it into resulting map creating new item or updating the existing one
   */

    private void doParseObject(JsonObject jsonObject, String tableName) {
        Map<String, String> values = new HashMap<>();
        Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        entrySet.forEach(stringJsonElementEntry -> {
            String key = stringJsonElementEntry.getKey();
            JsonElement value = stringJsonElementEntry.getValue();
            String val = "";
            if (value != null)
                val = value.getAsString();
            if (key != null && !key.isEmpty())
                StringWorker.trunc(key);
            if (val != null && !val.isEmpty())
                StringWorker.trunc(val);
            values.put(key, val);
        });
        String id = values.get("id");//keyfield is required
        Object object = this.objectMap.containsKey(id) ? this.objectMap.get(id) : metadata.create(entity);
        Map<String, String> fieldsMapping = this.tables.get(tableName);
        object = fillFields(object, fieldsMapping, values);
        this.objectMap.put(id, object);
    }

    /*
     * Fills fields of a given object and returns it filled
     */

    private Object fillFields(Object object, Map<String, String> fieldsMapping, Map<String, String> values) {
        fieldsMapping.forEach((property, field) -> {
            try {
                Method methods[] = this.entity.getDeclaredMethods();
                Method extMethods[] = new Method[methods.length + 1];
                int i = 0;
                for (Method method : methods) {
                    extMethods[i++] = method;
                }
                try {
                    extMethods[extMethods.length - 1] = this.entity.getMethod("getId");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                for (Method method : methods) {
                    if (method.getName().equals("set" + StringWorker.firstUpperCase(property))) {
                        String value = values.get(field);
                        if (value != null) {
                            Class[] classes = method.getParameterTypes();
                            for (Class clazz : classes) {
                                if (clazz.isAssignableFrom(String.class)) {
                                    method.invoke(object, value);
                                } else if (clazz.isAssignableFrom(Date.class)) {
                                    Date date = parseValueAsDate(value);
                                    if (date == null)
                                        date = parseValueAsMySqlTimestamp(value);
                                    method.invoke(object, date);
                                } else if (clazz.isAssignableFrom(Integer.class)) {
                                    Integer integerValue = Integer.parseInt(value);
                                    method.invoke(object, integerValue);
                                } else if (clazz.isEnum()) {
                                    if (clazz.isAssignableFrom(SiteExchangeEvent.class)) {
                                        SiteExchangeEvent event = SiteExchangeEvent.fromId(value);
                                        method.invoke(object, event);
                                    } else if (clazz.isAssignableFrom(SiteExchangeStatus.class)) {
                                        SiteExchangeStatus status = SiteExchangeStatus.fromId(value);
                                        method.invoke(object, status);
                                    }
                                } else if (clazz.isAssignableFrom(Boolean.class)) {
                                    Boolean boolVal = Integer.parseInt(value) == 1;
                                    method.invoke(object, boolVal);
                                } else if (clazz.isAssignableFrom(Entity.class)) {
                                    Object o = clazz.newInstance();
                                    UUID oId = UuidProvider.fromString(value);
                                    Method idMe = clazz.getMethod("setId");
                                    idMe.invoke(o, oId);
                                    method.invoke(object, o);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return object;
    }

    private Date parseValueAsDate(String value) {
        DateFormat ddf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date;
        try {
            date = ddf.parse(value);
        } catch (Exception e) {
            return null;
        }
        return date;
    }

    private Date parseValueAsMySqlTimestamp(String value) {
        Long dateInTimestamp;
        try {
            dateInTimestamp = Long.parseLong(value);
        } catch (Exception e) {
            return null;
        }
        return new Date(dateInTimestamp * 1000);
    }

    private void doParseArray(JsonArray jsonArray, String tableName) {
        Iterator<JsonElement> itr = jsonArray.iterator();
        int i = 0;
        while (itr.hasNext()) {
            JsonElement item = itr.next();
            if (item instanceof JsonObject) { // if a row appeared than we need to make a final map and return it
                JsonObject jsonObject = item.getAsJsonObject();
                doParseObject(jsonObject, tableName);
            } else if (item instanceof JsonArray) { // if an array appeared again than we need to make a multimap
                JsonArray mainArray = item.getAsJsonArray();
                doParseArray(mainArray, tableName);
            } else {  //If only one value appeared, i.e. count of rows
                // put somewhere "SingleItem", item.toString());
            }
        }
    }

    public Map<String, Object> parse(Map<String, String> jsonMap) {
        jsonMap.forEach((tableName, jsonMultipleRow) -> {
            //log.info("Table name "+tableName+"\n JSON to parse:"+jsonMultipleRow+"\n");
            doParseToMultiMap(jsonMultipleRow, tableName);
        });
        return this.objectMap;
    }

    public List<UUID> parseIdList(String jsonString, String fieldName) {
        if (jsonString == null || jsonString.length() == 0) {
            log.warn("JSON IS EMPTY for field " + fieldName);
            return null;
        }

        List<UUID> result = new ArrayList<>();
        try {
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();
            Iterator<JsonElement> itr = jsonArray.iterator();
            int i = 0;
            while (itr.hasNext()) {
                JsonElement item = itr.next();
                JsonObject jsonObject = item.getAsJsonObject();
                String id = jsonObject.get(fieldName).getAsString();
                result.add(UuidProvider.fromString(id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String> parseStringList(String jsonString, String fieldName) {
        if (jsonString == null || jsonString.length() == 0) {
            log.warn("JSON IS EMPTY for field " + fieldName);
            return null;
        }

        List<String> result = new ArrayList<>();
        try {
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();
            Iterator<JsonElement> itr = jsonArray.iterator();
            int i = 0;
            while (itr.hasNext()) {
                JsonElement item = itr.next();
                JsonObject jsonObject = item.getAsJsonObject();
                String field = jsonObject.get(fieldName).getAsString();
                result.add(field);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private void doParseToMultiMap(String jsonString, String tableName) {
        try {
            JsonParser parser = new JsonParser();
            //Object json = new JSONTokener(jsonString).nextValue();
            if (jsonString != null) {
                JsonArray mainArray = parser.parse(jsonString).getAsJsonArray();
                doParseArray(mainArray, tableName);
            } else {
                log.warn("JSON is empty from table " + tableName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
