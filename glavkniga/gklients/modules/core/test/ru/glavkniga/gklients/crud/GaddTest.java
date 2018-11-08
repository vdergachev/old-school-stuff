/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crud;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientService;
import ru.glavkniga.gklients.gconnection.Response;
import ru.glavkniga.gklients.gconnection.SendGet;
import ru.glavkniga.gklients.gconnection.Session;
import ru.glavkniga.gklients.gconnection.UrlFormer;
import ru.glavkniga.gklients.interfaces.WebsiteConfig;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.*;
import static ru.glavkniga.gklients.utils.TestUtils.givenClient;
import static ru.glavkniga.gklients.utils.TestUtils.givenClientService;

/**
 * Created by vdergachev on 24.05.17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AppBeans.class, SendGet.class, UrlFormer.class})
public class GaddTest {

    private final Gadd add = new Gadd();

    @Captor
    private ArgumentCaptor<Map<String, String>> dataCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(AppBeans.class);

        final Resources resources = mock(Resources.class);
        given(AppBeans.get(Resources.NAME)).willReturn(resources);
        when(resources.getResourceAsStream(Mapper.MAPPING_PATH))
                .thenReturn(this.getClass().getResourceAsStream(Mapper.MAPPING_PATH));

        final Configuration configuration = mock(Configuration.class);
        final WebsiteConfig websiteConfig = mock(WebsiteConfig.class);
        when(websiteConfig.getWebsiteURL()).thenReturn("http://somewhere:54321");
        given(AppBeans.get(Configuration.NAME)).willReturn(configuration);
        when(configuration.getConfig(WebsiteConfig.class)).thenReturn(websiteConfig);

        final Session session = mock(Session.class);
        given(AppBeans.get(Session.NAME)).willReturn(session);
        when(session.getSessionId()).thenReturn("session-id-123");

        PowerMockito.mockStatic(SendGet.class);
        final Response response = new Response();
        response.json = "1";
        when(SendGet.get(anyString())).thenReturn(response);
    }

    @Test
    public void successfullyAddsClient() {
        // given
        final Client client = givenClient();

        // when
        add.addObject(client);

        // then
    }

    @Test
    public void successfullyAddsClientService() {
        // given
        PowerMockito.spy(UrlFormer.class);
        final ClientService clientService = givenClientService();

        // when
        add.addObject(clientService);

        // then
        verifyStatic();
        UrlFormer.buildUrl(eq("add"), dataCaptor.capture());

        final Map<String, String> capturedData = dataCaptor.getValue();
        final String expectedUserId = clientService.getClient().getId().toString();
        assertThat(capturedData, hasEntry("ins[user_id]", expectedUserId));
    }

}
