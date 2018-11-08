/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.AbstractResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.glavkniga.gklients.gconnection.Session;
import ru.glavkniga.gklients.interfaces.WebsiteConfig;
import ru.glavkniga.gklients.networking.WebSiteServiceError;
import ru.glavkniga.gklients.networking.WebSiteServiceException;
import ru.glavkniga.gklients.networking.api.model.UploadAttachmentResponse;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static ru.glavkniga.gklients.networking.WebSiteServiceError.FILE_UPLOAD_FAILURE;
import static ru.glavkniga.gklients.networking.WebSiteServiceError.LOGIN_FAILURE;

/**
 * Created by vdergachev on 03.07.17.
 */
@Service(SiteFileUploadService.NAME)
public class SiteFileUploadServiceBean implements SiteFileUploadService {

    // TODO Add logging
    private static Logger log = LoggerFactory.getLogger(SiteFileUploadServiceBean.class);

    private final RestTemplate client = new RestTemplate();

    @Inject
    private WebsiteConfig config;

    @Inject
    private FileStorageAPI fileStorageAPI;

    private String baseUrl;
    private String uploadAttachmentPath;

    @PostConstruct
    private void init() {
        final SimpleClientHttpRequestFactory requestFactory =
                (SimpleClientHttpRequestFactory) client.getRequestFactory();

        requestFactory.setConnectTimeout(config.getConnectTimeout());
        requestFactory.setReadTimeout(config.getReadTimeout());

        baseUrl = config.getWebsiteURL();
        uploadAttachmentPath = config.getAttachmentUploadPath();
    }


    @Override
    public URL uploadAttachment(final byte[] fileContent, final String filename) throws WebSiteServiceException {
        List<HttpMessageConverter<?>> messageConverters = client.getMessageConverters();
        for(HttpMessageConverter httpMessageConverter : messageConverters){
            if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) httpMessageConverter;
                jsonConverter.setObjectMapper(new ObjectMapper());
                jsonConverter.setSupportedMediaTypes(ImmutableList.of(new MediaType("text", "html", MappingJackson2HttpMessageConverter.DEFAULT_CHARSET)));
            }
        }

        final HttpEntity<MultiValueMap<String, Object>> request = createHttpEntityWithFileData(fileContent, filename);

        ResponseEntity<UploadAttachmentResponse> response;
        try {
            response = client.exchange(buildUrl(uploadAttachmentPath),
                    HttpMethod.POST,
                    request,
                    UploadAttachmentResponse.class);
        } catch (RestClientException ex) {
            throw new WebSiteServiceException(FILE_UPLOAD_FAILURE, ex.getCause());
        }

        // TODO validate response instead direct error code check
        final HttpStatus status = response.getStatusCode();
        if (status != HttpStatus.OK && status != HttpStatus.NOT_FOUND) {
            throw new WebSiteServiceException(FILE_UPLOAD_FAILURE);
        }

        if (status == HttpStatus.NOT_FOUND || response.getBody() == null) {
            throw new WebSiteServiceException(FILE_UPLOAD_FAILURE);
        }

        try {
            return new URL(response.getBody().getPath());
        } catch (MalformedURLException ex) {
            throw new WebSiteServiceException(WebSiteServiceError.URL_PARSE_FAILURE, ex);
        }
    }

    private HttpEntity<MultiValueMap<String, Object>> createHttpEntityWithFileData(final byte[] fileContent,
                                                                                   final String filename) {
        final LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.put("file", ImmutableList.of(new FileContentResource(fileContent, filename)));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        return new HttpEntity<>(map, headers);
    }

    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setUploadAttachmentPath(final String uploadAttachmentPath) {
        this.uploadAttachmentPath = uploadAttachmentPath;
    }

    private String buildUrl(final String path) {

        // TODO Move url building stuff to separate class and store sessionId in client
        final Session session = AppBeans.get(Session.NAME);

        // TODO Refactor it
        String sessionId;
        try {
            sessionId = session.getSessionId();
        } catch (RuntimeException ex) {
            throw new WebSiteServiceException(LOGIN_FAILURE);
        }

        final UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/")
                .path(path)
                .query("s={sessionId}").buildAndExpand(sessionId);

        return uriComponents.toString();
    }


    private static class FileContentResource extends AbstractResource {
        private final byte[] fileContent;
        private final String filename;

        public FileContentResource(final byte[] fileContent, final String filename) {
            this.fileContent = fileContent;
            this.filename = filename;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(fileContent);
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public long contentLength() throws IOException {
            return fileContent.length;
        }
    }

    public URL uploadAttachment(final FileDescriptor descriptor) {
        try {
            final byte[] byteArray = fileStorageAPI.loadFile(descriptor);
            return uploadAttachment(byteArray, descriptor.getName());
        } catch (FileStorageException e) {
            return null;
        }
    }

}
