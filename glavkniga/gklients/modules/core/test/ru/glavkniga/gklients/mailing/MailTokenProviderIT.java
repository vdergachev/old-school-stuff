package ru.glavkniga.gklients.mailing;

import com.google.common.collect.ImmutableList;
import com.haulmont.cuba.core.global.AppBeans;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.glavkniga.gklients.BaseIT;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.Token;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static ru.glavkniga.gklients.utils.TestUtils.givenClient;
import static ru.glavkniga.gklients.utils.TestUtils.givenToken;

/**
 * Created by vdergachev on 11.07.17.
 */

public class MailTokenProviderIT extends BaseIT {

    private MailTokenProvider provider;

    private static Client client;
    private static Token token;

    @BeforeClass
    public static void setUp() {
        cleanUpDB();

        client = save(givenClient());
        token = save(givenToken("NAME"));
    }


    @After
    public void tearDown() {
        container.deleteRecord(client);
        container.deleteRecord(token);
    }


    @Before
    public void init() {
        provider = AppBeans.get(MailTokenProvider.class);
    }

    @Test
    public void provideSucceed() {
        // given
        final Context context = new Context().withClient(client);

        // when
        final Map<String, String> values = provider.provide(ImmutableList.of(ParsedToken.parse("NAME")), context);

        // then
        assertThat(values, hasEntry("NAME", context.client.getName()));
    }

}
