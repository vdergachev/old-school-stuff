package ru.glavkniga.gklients.rule.test;

import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import ru.glavkniga.gklients.rule.SiteRule;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;

/**
 * Created by Vladimir on 27.05.2017.
 */

//TODO Add all required tests
public class SiteRuleTest {

    @ClassRule
    public static SiteRule server = new SiteRule("localhost", 38888);

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void serverSuccessfullyHandlesAddRequest() {
        // given
        final String url = "http://localhost:38888/app/add?a=key&b=value";

        // when
        final String getResult = restTemplate.getForObject(url, String.class);

        // then
        assertThat(getResult, is("1"));
    }

    @Test
    public void serverSuccessfullyHandlesLoginRequest() {
        // given
        final String url = "http://localhost:38888/app/login?u=user&p=password";

        // when
        final Map<String, Object> getResult = restTemplate.getForObject(url, Map.class);

        // then
        assertThat(getResult, hasKey("session_id"));
    }
}
