/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.service;

import com.haulmont.cuba.core.entity.FileDescriptor;
import ru.glavkniga.gklients.networking.WebSiteServiceException;

import java.net.URL;

/**
 * Created by vdergachev on 03.07.17.
 */
public interface SiteFileUploadService {
    String NAME = "gklients_SiteFileUploadService";

    URL uploadAttachment(final byte[] fileContent, final String filename) throws WebSiteServiceException;

    URL uploadAttachment(final FileDescriptor descriptor);

    // TODO Тут должно быть описано все взаимодействие с сайтом ГК
}
