package ru.glavkniga.gklients.mailing;

import com.google.common.collect.ImmutableMap;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.glavkniga.gklients.entity.Client;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static ru.glavkniga.gklients.mailing.TemplateProcessor.TemplateProcessorException;

/**
 * Created by vdergachev on 10.07.17.
 */
public class TemplateProcessorProcessTest {

    @InjectMocks
    public TemplateProcessor processor = new TemplateProcessorImpl();

    @Mock
    public MailTokenProviderImpl tokenProvider;

    @DataProvider
    public Object[][] correctTemplates() {
        return new Object[][]{
                {"", ""},
                {"${token}", "token-value"},
                {" ${token}\n${token}", " token-value\ntoken-value"},
                {"${token}${token}", "token-valuetoken-value"},
                {"${token} ${token}", "token-value token-value"}
        };
    }

    @DataProvider
    public Object[][] correctTemplatesWithDefaults() {
        return new Object[][]{
                {"${token1 | \"\"}", ""},
                {"${token1 | \"another-value\"}", "another-value"},
                {"${token1 | \"another-value\"} ${token2 | \"test\"}", "another-value test"}
        };
    }

    @DataProvider
    public Object[][] incorrectTemplates() {
        return new Object[][]{
                {"${"},
                {"}"},
                {"${${}"},
                {"${}}"},
                {"token}${token"},
                {"${${token}}"},
                {"${{token}}"}
        };
    }

    @DataProvider
    public Object[][] exceptionRaisingTemplates() {
        return new Object[][]{
                {"${to ken} ${token}"},
        };
    }

    @BeforeMethod(alwaysRun = true)
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(dataProvider = "correctTemplates")
    public void processSucceedWithResult(final String template, String expectedResult) {
        // given
        final Context context = new Context().withClient(new Client());

        givenTokenProvider(context, ImmutableMap.of("token", "token-value"));

        // when
        final String actualResult = processor.process(template, context);

        // then
        assertThat(actualResult, is(expectedResult));
    }

    @Test(dataProvider = "correctTemplatesWithDefaults")
    public void processSucceedWithResultWhenTokensHaveDefaults(final String template, String expectedResult) {
        // given
        final Context context = new Context().withClient(new Client());

        final Map<String, String> tokenValues = newHashMap();
        tokenValues.put("token1", null);
        tokenValues.put("token2", null);

        givenTokenProvider(context, tokenValues);

        // when
        final String actualResult = processor.process(template, context);

        // then
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void processThrowsExceptionWithListOfIncorrectTokens() {
        // given
        final String template = "${to ken} ${token}";

        final Context context = new Context().withClient(new Client());

        givenTokenProvider(context, ImmutableMap.of("token", "token-value"));

        // when
        TemplateProcessorException ex = null;
        try {
            processor.process(template, context);
        } catch (TemplateProcessorException e) {
            ex = e;
        }

        // then
        assertThat(ex, notNullValue());
        assertThat(ex.getWrongTokens(), hasItem("${to ken}"));
    }


    @Test(dataProvider = "incorrectTemplates")
    public void processThrowsWhenTemplateIncorrect(final String template) {
        // given

        // when
        IllegalArgumentException ex = null;
        try {
            processor.process(template, null);
        } catch (IllegalArgumentException e) {
            ex = e;
        }

        // then
        assertThat(ex, notNullValue());
    }

    @Test
    public void processThrowsWhenNullPassedAsParameter() {
        // given

        // when
        RuntimeException ex = null;
        try {
            processor.process(null, null);
        } catch (RuntimeException e) {
            ex = e;
        }

        // then
        assertThat(ex, notNullValue());
    }

    private void givenTokenProvider(final Context context, final Map<String, String> tokenValues) {
        when(tokenProvider.provide(anyListOf(ParsedToken.class), eq(context))).thenReturn(tokenValues);
    }
}
