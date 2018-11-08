/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.crud;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import ru.glavkniga.gklients.BaseIT;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientService;
import ru.glavkniga.gklients.rule.SiteRule;

import static ru.glavkniga.gklients.utils.TestUtils.givenClient;
import static ru.glavkniga.gklients.utils.TestUtils.givenClientService;

/**
 * Created by vdergachev on 26.05.17.
 */
public class GaddIT extends BaseIT {

    @ClassRule
    public static SiteRule server = new SiteRule("localhost", 38888);

    private final Gadd add = new Gadd();

    private static Client client;
    private static ClientService clientService;

    @BeforeClass
    public static void setUp() {

        cleanUpDB();

        client = save(givenClient());
        clientService = save(givenClientService(client));
    }

    @After
    public void tearDown() {
        container.deleteRecord(clientService);
        container.deleteRecord(client);
    }

    @Test
    public void successfullyAddsClient() {
        //given

        // when
        add.addObject(client);

        // then

    }

    @Test
    public void successfullyAddsClientService() {
        //given

        // when
        add.addObject(clientService);

        // then
    }

}
