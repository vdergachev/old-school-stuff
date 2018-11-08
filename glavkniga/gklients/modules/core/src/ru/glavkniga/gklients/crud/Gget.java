/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crud;

/**
 * Created by LysovIA on 09.02.2017.
 */

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.glavkniga.gklients.gconnection.Response;
import ru.glavkniga.gklients.gconnection.SendGet;
import ru.glavkniga.gklients.gconnection.Session;
import ru.glavkniga.gklients.gconnection.UrlFormer;
import ru.glavkniga.gklients.interfaces.WebsiteConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * http://dev4.glavnayakniga.ru/app/get
 * ?
 * table=gk_sys_site_user
 * &
 * filter[email]=dfg@dfg.dfg
 * &
 * fields[]=id
 * &
 * fields[]=email
 */
public class Gget {

    private Configuration configuration = AppBeans.get(Configuration.NAME);
    private Map<String, String> filterFields = new HashMap<>();
    //  private Metadata metadata = AppBeans.get(Metadata.NAME);
    private Session s = AppBeans.get(Session.NAME);
    private String websiteURL = configuration.getConfig(WebsiteConfig.class).getWebsiteURL();
    //Store class for which we do parsing
    private Class<? extends Entity> entity;

    private Map<String, Map<String, String>> tableMap;
    private String requestURL;


    //private Logger log = LoggerFactory.getLogger(getClass());

    public Gget(Class<? extends Entity> entity) {
        Mapper mapper = new Mapper(entity);
        this.tableMap = mapper.getTableMap();
        this.entity = entity;
    }

    public void addFilterProperty(String propertyName, String value) {
        String fieldName = null;
        for (Map<String, String> fieldMap : tableMap.values()) {
            if (fieldMap.containsKey(propertyName)) {
                fieldName = fieldMap.get(propertyName);
                break;
            }
        }
        if (fieldName != null) {
            this.filterFields.put("filter[" + fieldName + "]", value);
        }
    }


    public void addFilterProperty(String propertyName, String value, Operation operation) {
        String fieldName = null;
        for (Map<String, String> fieldMap : tableMap.values()) {
            if (fieldMap.containsValue(propertyName)) {
                fieldName = fieldMap.get(propertyName);
                break;
            }
        }
        if (fieldName != null) {
            this.filterFields.put("filter[" + fieldName + "][" + operation.getId() + "]", value);
        }
    }

    public void addFilterField(String fieldName, String value, Operation operation) {
        switch (operation) {
            case IN:
            case NOT_IN:
                int i = 0;
                String[] split = value.split(",");
                for (String splitVal : split) {
                    this.filterFields.put("filter[" + fieldName + "][" + operation.getId() + "][" + i++ + "]", splitVal);
                }
                break;
            default:
                this.filterFields.put("filter[" + fieldName + "][" + operation.getId() + "]", value);
        }

    }


    public String getJson(String tableName, Map<String, String> fields, Map<String, String> filterFields) {

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("table", tableName);
        urlParams.putAll(fields);
        urlParams.putAll(filterFields);

        urlParams.put("s", s.getSessionId());
        this.requestURL = UrlFormer.addParams(websiteURL + "get", urlParams);
      //  log.info(requestURL);
        Response response = SendGet.get(requestURL);
        return response.json;
    }

    public Map<String, Object> getObjects() {
        Map<String, String> jsonMap = new HashMap<>();
        tableMap.forEach((tableName, fieldMap) -> {
            Map<String, String> fields = new HashMap<>();
            if (fieldMap != null)
                fieldMap.forEach((o, o2) -> fields.put("fields[" + o2 + "]", o2));
            jsonMap.put(tableName, this.getJson(tableName, fields, this.filterFields));
        });
        Parser parser = new Parser(entity);
        return parser.parse(jsonMap);
    }

    public List<UUID> getIdList(String propertyName) {
        String tableName = tableMap.keySet().iterator().next(); //take first table
        String fieldName = tableMap.get(tableName).get(propertyName); //take field name for given property name
        Map<String, String> fields = new HashMap<>();
        fields.put("fields[" + fieldName + "]", fieldName);
        String json = this.getJson(tableName, fields, this.filterFields);
        if (json != null) {
            Parser parser = new Parser(entity);
            return parser.parseIdList(json, fieldName);
        } else return null;
    }

    public List<String> getFieldList(String propertyName) {
        String tableName = tableMap.keySet().iterator().next(); //take first table
        String fieldName = tableMap.get(tableName).get(propertyName); //take field name for given property name
        Map<String, String> fields = new HashMap<>();
        fields.put("fields[" + fieldName + "]", fieldName);
        String json = this.getJson(tableName, fields, this.filterFields);
        if (json != null) {
            Parser parser = new Parser(entity);
            return parser.parseStringList(json, fieldName);
        } else return null;
    }

}
