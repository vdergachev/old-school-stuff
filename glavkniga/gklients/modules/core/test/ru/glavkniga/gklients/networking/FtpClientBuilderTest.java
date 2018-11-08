/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.networking;

import org.junit.Test;
import ru.glavkniga.gklients.interfaces.FtpServerConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by vdergachev on 13.06.17.
 */
public class FtpClientBuilderTest {

    @Test
    public void buildSucceed() {

        // given
        final FtpClient.FtpClientBuilder builder = new FtpClient.FtpClientBuilder()
                .withHostAndPort("host", 123)
                .withUserCredentials("username", "password");

        // when
        final FtpClient client = builder.build();

        // then
        assertThat(client, notNullValue());
        assertThat(client.getHost(), is("host"));
        assertThat(client.getPort(), is(123));
        assertThat(client.getUsername(), is("username"));
        assertThat(client.getPassword(), is("password"));
        assertThat(client.isAuthorized(), is(false));
    }

    @Test
    public void buildWithConfigurationSucceed() {

        // given
        final FtpClient.FtpClientBuilder builder = new FtpClient.FtpClientBuilder()
                .withConfiguration(givenFtpServerConfig("host", 123, "username", "password"));

        // when
        final FtpClient client = builder.build();

        // then
        assertThat(client, notNullValue());
        assertThat(client.getHost(), is("host"));
        assertThat(client.getPort(), is(123));
        assertThat(client.getUsername(), is("username"));
        assertThat(client.getPassword(), is("password"));
        assertThat(client.isAuthorized(), is(false));
    }

    private FtpServerConfig givenFtpServerConfig(final String host, final int port, final String username,
                                                 final String password) {
        return new FtpServerConfig() {
            @Override
            public String getHost() {
                return host;
            }

            @Override
            public Integer getPort() {
                return port;
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getPassword() {
                return password;
            }
        };
    }

}
