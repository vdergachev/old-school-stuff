package io.playtech.spliturl.algorithms;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(JUnitParamsRunner.class)
public class AlgoTest {

    // JUnitParams allows save space and time and have only one "wide" test
    private Object[] validUrls() {
        return new Object[][]{
                {"http://mail.ru/path?param1=val1", result("http", "mail.ru", 80, "/path", "param1=val1")},
                {"http://mail.ru/path?", result("http", "mail.ru", 80, "/path", "")},
                {"http://mail.ru/path", result("http", "mail.ru", 80, "/path", "")},
                {"http://mail.ru", result("http", "mail.ru", 80, "/", "")},
                {"http://mail.ru/", result("http", "mail.ru", 80, "/", "")},
                {"https://ya.ru:443/poor?param2=val2", result("https", "ya.ru", 443, "/poor", "param2=val2")},
        };
    }

    @Test
    @Parameters(method = "validUrls")
    public void algorithmsParsesValidURL(final String url, final Result expectedResult) {

        // given
        final RegexAlgorithm regexAlgorithm = new RegexAlgorithm();
        final StateAlgorithm stateAlgorithm = new StateAlgorithm();

        // when
        final Result regexResult = regexAlgorithm.split(url);
        final Result stateResult = stateAlgorithm.split(url);

        // then
        assertThat(regexResult, is(expectedResult));
        assertThat(regexResult, is(stateResult));
    }

    private static Result result(String scheme, String host, int port, String path, String params) {
        return new Result()
                .withScheme(scheme)
                .withHost(host)
                .withPort(port)
                .withPath(path)
                .withParameters(params);
    }


}
