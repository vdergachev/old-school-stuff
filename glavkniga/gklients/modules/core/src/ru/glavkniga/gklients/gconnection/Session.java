/*
 * Copyright (c) 2015 gklients
 */

package ru.glavkniga.gklients.gconnection;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.TimeSource;
import org.springframework.stereotype.Component;
import ru.glavkniga.gklients.interfaces.WebsiteConfig;

import java.net.HttpURLConnection;
import java.util.HashMap;

/**
 * Created by LysovIA on 08.12.2015.
 */
// TODO Move all this stuff to web site service client
@Component(Session.NAME)
public class Session {

    public static final String NAME = "gklients_Session";
    //  private String url;

    private String sessionId;   // TODO make it volatile cuz shit can happened with this var
    // TODO Probably is better to assign countdown timer to sessionId and remove this vars
    private long sessionStartTime = 0;
    private final long SESSION_LIFETIME = (5 * 60 * 1000);

    private long delay() {
        TimeSource timeSource = AppBeans.get(TimeSource.class);
        if (this.sessionStartTime > 0)
            return timeSource.currentTimeMillis() - this.sessionStartTime;
        else
            return SESSION_LIFETIME + 1;
    }

    public String getSessionId() {
        if (this.sessionId == null || (delay() > SESSION_LIFETIME)) {
            this.sessionId = this.logIn();
        }
        return sessionId;
    }

    private String logIn() {
        // TODO Remove it
        Configuration configuration = AppBeans.get(Configuration.class);
        String websiteURL = configuration.getConfig(WebsiteConfig.class).getWebsiteURL();
        String websiteLogin = configuration.getConfig(WebsiteConfig.class).getWebsiteLogin();
        String websitePass = configuration.getConfig(WebsiteConfig.class).getWebsitePass();

        HashMap<String, String> params = new HashMap<>();
        params.put("u", websiteLogin);
        params.put("p", websitePass);
        String requestURL = websiteURL + MethodEnum.login.name();
        requestURL = UrlFormer.addParams(requestURL, params);
        Response response = SendGet.get(requestURL);

        TimeSource timeSource = AppBeans.get(TimeSource.class);

        Parser resPars = new Parser(response, MethodEnum.login);
        if (resPars.getResponseCode() == HttpURLConnection.HTTP_OK) {
            this.sessionStartTime = timeSource.currentTimeMillis();
            return resPars.getResponseMap().get("sessionId").toString();
        } else {
            return null;
        }
    }

    private void logOut() {
        Configuration configuration = AppBeans.get(Configuration.class);
        String websiteURL = configuration.getConfig(WebsiteConfig.class).getWebsiteURL();

        HashMap<String, String> params = new HashMap<>();
        params.put("s", this.sessionId);
        String requestURL = websiteURL + MethodEnum.login.name();
        requestURL = UrlFormer.addParams(requestURL, params);
        Response response = SendGet.get(requestURL);
        Parser resPars = new Parser(response, MethodEnum.logout);
        if (resPars.getResponseCode() == HttpURLConnection.HTTP_OK) {
            this.sessionId = null;
        } else {
            //TODO logout failed. Do something with it
        }
    }

}
