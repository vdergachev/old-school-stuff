/*
 * Copyright (c) 2015 gklients
 */

package ru.glavkniga.gklients.interfaces;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;

/**
 * Created by LysovIA on 15.12.2015.
 */
@Source(type = SourceType.APP)
public interface WebsiteConfig extends Config {

    @Property("gklients.website.baseURL")
    String getWebsiteURL();

    @Property("gklients.website.attachmentUploadPath")
    String getAttachmentUploadPath();

    @Property("gklients.website.login")
    String getWebsiteLogin();

    @Property("gklients.website.password")
    String getWebsitePass();

    @Property("gklients.website.connectTimeout")
    int getConnectTimeout();

    @Property("gklients.website.readTimeout")
    int getReadTimeout();

}
