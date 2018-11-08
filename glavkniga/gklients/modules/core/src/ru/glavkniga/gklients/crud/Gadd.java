/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crud;

/**
 * Created by LysovIA on 09.02.2017.
 */

import com.haulmont.chile.core.datatypes.impl.EnumClass;
import com.haulmont.cuba.core.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import ru.glavkniga.gklients.gconnection.Response;
import ru.glavkniga.gklients.gconnection.SendGet;
import ru.glavkniga.gklients.gconnection.UrlFormer;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Sample URL
 * http://dev4.glavnayakniga.ru/app/add
 * ?table=gk_sys_site_user
 * &ins[id]=2833d47d-5eb5-20cd-f658-fd1f3435e7b2
 * &ins[email]=asd@asd.asd
 * &ins[pass_hash]=78923h6f789658903570389
 * &mode=update
 * &return_rows=1
 * <p>
 * mode=update
 * mode=ignore
 * <p>
 * <p>
 * return_rows=1
 */

public class Gadd {

    private Logger log = LoggerFactory.getLogger(Gadd.class);

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    protected Map<String, Map<String, String>> tableMap;
    protected Map<String, String> dataFields = new HashMap<>();
    protected Map<String, String> filterFields = new HashMap<>();

    protected String action = "add";

    private String mode = null;

    public Gadd() {

    }

    public void setDuplicateIgnoreMode(final boolean onOff) {
        mode = onOff ? "ignore" : "update";
    }

    public boolean addObject(final Entity object) {

        final boolean[] result = {true};

        final Class<? extends Entity> clazz = object.getClass();
        tableMap = new Mapper(clazz).getTableMap();

        tableMap.forEach((tableName, mappingTable) -> {
            dataFields.clear();
            if (mode != null) {
                dataFields.put("mode", mode);
            }

            dataFields.put("table", tableName);
            dataFields.put("return_rows", "1");
            dataFields.putAll(filterFields);

            final List<Method> clazzMethods = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(m -> m.getName().startsWith("get"))
                    .collect(Collectors.toList());

            clazzMethods.add(ReflectionUtils.findMethod(clazz, "getId"));

            for (final Method method : clazzMethods) {
                final String methodName = method.getName();
                String resultValue = "";

                Object invokeResult = null;
                try {
                    invokeResult = method.invoke(object);
                } catch (Exception ignored) {
                }

                if (invokeResult != null) {
                    if (invokeResult instanceof Entity) {
                        resultValue = ((Entity) invokeResult).getId().toString();
                    } else if (invokeResult instanceof EnumClass) {
                        resultValue = ((EnumClass) invokeResult).getId().toString();
                    } else if (invokeResult instanceof Date) {
                        resultValue = DATE_FORMAT.format((Date) invokeResult);
                    } else if (invokeResult instanceof Boolean) {
                        resultValue = (Boolean) invokeResult ? "1" : "0";
                    } else {
                        resultValue = invokeResult.toString();
                    }
                }
                final String propertyName = StringWorker.firstLowerCase(StringWorker.cutSubstr(methodName, "get"));
                final String fieldName = mappingTable.get(propertyName);

                if (!isNullOrEmpty(resultValue) && !isNullOrEmpty(fieldName)) {
                    switch (action) {
                        case "add":
                            dataFields.put("ins[" + fieldName + "]", resultValue);
                            break;
                        case "edit":
                            dataFields.put("upd[" + fieldName + "]", resultValue);
                            break;
                    }
                }
            }

            final String url = UrlFormer.buildUrl(action, dataFields);
            final Response response = SendGet.get(url);
            result[0] = result[0] && Objects.equals("1", StringWorker.trunc(response.json));
        });

        return result[0];
    }
}
