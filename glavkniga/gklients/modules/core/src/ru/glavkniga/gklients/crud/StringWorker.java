/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crud;

import java.util.Objects;
import java.util.Random;

/**
 * Created by LysovIA on 14.02.2017.
 */
public class StringWorker {
    /*
    * makes first char of property to be capital to fit camelCase notation of method names
    * */

    public static String firstUpperCase(String word) {
        if (word == null || word.isEmpty())
            return word;
        return
                word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    /*
    * makes first char of property not to be capital to fit camelCase notation of method names
    * */

    public static String firstLowerCase(String word) {
        if (word == null || word.isEmpty())
            return word;
        return
                word.substring(0, 1).toLowerCase() + word.substring(1);
    }

    /*
    * Removes surrounding " symbol from string value
    * */

    public static String trunc(String res) {

        if (Objects.isNull(res)) {
            return null;
        }

        if (res.charAt(0) == '\"') {
            res = res.substring(1, res.length());
        }
        if (res.charAt(res.length() - 1) == '\"') {
            res = res.substring(0, res.length() - 1);
        }

        if (res.equals("null") || res.equals("Null")) {
            res = "";
        }

        return res;
    }

    public static String cutSubstr(String fromWhich, String what) {
        return fromWhich.replaceFirst(what, "");
    }

}
