/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crud;

import com.haulmont.cuba.core.entity.Entity;

/**
 * Created by LysovIA on 09.02.2017.
 */

/*
* Sample URL
* http://dev4.glavnayakniga.ru/app/edit
* ? table=gk_sys_site_user
* & upd[email]=dfg@dfg.dfg
* & filter[id]=3f5-34g5-34g5-343
* */

public class Gedit extends Gadd{

    protected final static String ACTION = "edit";

    public Gedit() {

    }


    public void addFilterField(String name, String value) {
        filterFields.put("filter[" + name + "]", value);
    }


  //  @Override
    public void addDataField(String name, String value) {
        this.dataFields.put("upd["+name+"]", value);
    }

//    public String edit() {
//        String websiteURL = configuration.getConfig(WebsiteConfig.class).getWebsiteURL();
//
//        if (this.filterFields == null){
//            return null;
//        }
//        Map <String,String> urlParam = this.dataFields;
//        urlParam.putAll(this.filterFields);
//            this.dataFields.put("s", s.getSessionId());
//            String requestURL = UrlFormer.addParams(websiteURL + "edit", urlParam);
//            Response response = SendGet.get(requestURL);
//            return response.json;
//    }

    public boolean editObject(Entity object){
        this.action = "edit";
        return addObject(object);
    }


}
