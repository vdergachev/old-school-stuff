/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.utils;

import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientService;
import ru.glavkniga.gklients.entity.Token;

import java.util.Date;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by vdergachev on 26.05.17.
 */
public class TestUtils {

    private TestUtils() {
        // can't be created
    }

    public static ClientService givenClientService() {
        return givenClientService(null);
    }

    public static ClientService givenClientService(final Client client) {
        return initClientService(new ClientService(), client != null ? client : givenClient());
    }

    public static Client givenClient() {
        return initClient(new Client());
    }

    public static Token givenToken(final String token) {
        return initToken(new Token(), token);
    }

    private static Token initToken(final Token token, final String name) {
        token.setId(UUID.randomUUID());
        token.setName("test token \"" + name + "\"");
        token.setToken(name);
        token.setIsPersonal(true);

        return token;
    }

    private static ClientService initClientService(final ClientService clientService,
                                                   final Client client) {
        clientService.setClient(client);
        clientService.setActivationDate(new Date());
        clientService.setService("1");

        return clientService;
    }

    private static Client initClient(final Client client) {
        client.setId(UUID.randomUUID());
        client.setItn("client-itn");
        client.setName("client-name");
        client.setPassword("strong-password-123");
        client.setPhone("+7123456789");
        client.setEmail("some@say.com");
        return client;
    }

    public static Properties loadProperties(final String path) {
        final Properties properties = new Properties();
        try {
            properties.load(TestUtils.class.getClassLoader().getResourceAsStream(path));
        } catch (final Exception ex) {
            throw new RuntimeException("Can't load properties from file " + path, ex);
        }
        return properties;
    }
}
