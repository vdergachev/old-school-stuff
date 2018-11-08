/*
 * Copyright (c) 2015 gklients
 */

package ru.glavkniga.gklients.gconnection;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.glavkniga.gklients.interfaces.WebsiteConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by LysovIA on 09.12.2015.
 */
public class UrlFormer {

    private static Logger log = LoggerFactory.getLogger(UrlFormer.class);

    private final static Configuration configuration = AppBeans.get(Configuration.NAME);
    private final static String WEBSITE_URL = configuration.getConfig(WebsiteConfig.class).getWebsiteURL();

    public static String buildUrl(final String action, final Map<String, String> params) {
        // add session information before serialize result url
        params.put("s", ((Session) AppBeans.get(Session.NAME)).getSessionId());
        final String baseUrl = WEBSITE_URL + (WEBSITE_URL.endsWith("/")?"":"/");
        final StringBuilder result = new StringBuilder(baseUrl + action);
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
                result.append("?");
            } else {
                result.append("&");
            }

            result.append(encode(entry.getKey()));
            result.append("=");
            result.append(encode(entry.getValue()));
        }
        return result.toString();
    }

    private static String encode(final String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.error("Can't encode value \"" + value + "\"", ex);
        }
        return "";
    }


    public static String addParams(String url, Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        result.append(url);

        boolean first = true;
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    first = false;
                    result.append("?");
                } else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(); //TODO this means GET not working at all. Stop the service!
        }

        return result.toString();

    }

}
