/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crud;

import com.haulmont.chile.core.datatypes.impl.EnumClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import ru.glavkniga.gklients.gconnection.Response;
import ru.glavkniga.gklients.gconnection.SendGet;
import ru.glavkniga.gklients.gconnection.Session;
import ru.glavkniga.gklients.gconnection.UrlFormer;
import ru.glavkniga.gklients.interfaces.WebsiteConfig;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by LysovIA on 09.02.2017.
 */

/*
* http://dev4.glavnayakniga.ru/app/del
* ? table=gk_sys_site_user
* & filter[email]=dfg@dfg.dfg
* */


public class Gdelete {

    private Configuration configuration = AppBeans.get(Configuration.NAME);
    private Map<String, String> dataFields = new HashMap<>();
    private Map<String, String> filterFields = new HashMap<>();
    private Session s = AppBeans.get(Session.NAME);
    private boolean result;

    private Map<String, Map<String, String>> tableMap;

    public void addFilterField(String name, String value) {
        this.filterFields.put("filter[" + name + "]", value);
    }


    public Gdelete(Class<? extends Entity> objectType) {
        Mapper mapper = new Mapper(objectType);
        this.tableMap = mapper.getTableMap();
    }


    public static boolean deleteRecord(String tableName, HashMap<String, String> filterFieldsWithValues) {
        Configuration staticConfiguration = AppBeans.get(Configuration.NAME);
        String websiteURL = staticConfiguration.getConfig(WebsiteConfig.class).getWebsiteURL();

        Map<String, String> dataFields = new HashMap<>();
        dataFields.put("table", tableName);

        filterFieldsWithValues.forEach((key, value) -> dataFields.put("filter[" + key + "]", value));

        Session staticSession = AppBeans.get(Session.NAME);
        dataFields.put("s", staticSession.getSessionId());

        String requestURL = UrlFormer.addParams(websiteURL + "del", dataFields);
        Response response = SendGet.get(requestURL);
        return StringWorker.trunc(response.json).equals("1");
    }


    public boolean deleteObject(Object object) {
        String websiteURL = configuration.getConfig(WebsiteConfig.class).getWebsiteURL();
        Class thisclass = object.getClass();
        this.tableMap.forEach((tableName, fieldMap) -> {
            this.dataFields.clear();
            this.dataFields.put("table", tableName);
            Method methods[] = thisclass.getDeclaredMethods();
            Method extMethods[] = new Method[methods.length + 1];
            int i = 0;
            for (Method method : methods) {
                extMethods[i++] = method;
            }
            try {
                extMethods[extMethods.length - 1] = thisclass.getMethod("getId");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            for (Method method : extMethods) {
                String methodName = method.getName();
                if (methodName.startsWith("get")) {
                    String value = "";
                    try {
                        Object tempObj = method.invoke(object);
                        if (tempObj != null) {
                            if (tempObj instanceof Entity) {
                                Method idMe = Entity.class.getMethod("getId");
                                value = String.valueOf(idMe.invoke(tempObj));
                            } else if (tempObj instanceof EnumClass) {
                                Method idMe = EnumClass.class.getMethod("getId");
                                value = String.valueOf(idMe.invoke(tempObj));
                            } else if (tempObj instanceof Date) {
                                DateFormat ddf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                value = ddf.format((Date) tempObj);
                            } else
                                value = String.valueOf(tempObj);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String propertyName = StringWorker.firstLowerCase(StringWorker.cutSubstr(methodName, "get"));
                    String fieldName = fieldMap.get(propertyName);
                    if (value != null && !value.isEmpty() && fieldName != null && !fieldName.isEmpty()) {
                        this.dataFields.put("filter[" + fieldName + "]", value);
                    }
                }
            }
            this.dataFields.put("s", s.getSessionId());
            this.dataFields.putAll(this.filterFields);
            String requestURL = UrlFormer.addParams(websiteURL + "del", this.dataFields);
            Response response = SendGet.get(requestURL);
            this.result = this.result && StringWorker.trunc(response.json).equals("1");
        });
        return this.result;
//        if (this.filterFields == null){
//            return null;
//        }
//
//        Map <String,String> urlParam = this.dataFields;
//        urlParam.putAll(this.filterFields);
//            this.dataFields.put("s", s.getSessionId());
//            String requestURL = UrlFormer.addParams(websiteURL + "del", urlParam);
//            Response response = SendGet.get(requestURL);
//            return response.json;
    }

}
