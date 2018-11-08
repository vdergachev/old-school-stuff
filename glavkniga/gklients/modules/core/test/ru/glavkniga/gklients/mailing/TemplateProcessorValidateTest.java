package ru.glavkniga.gklients.mailing;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by Vladimir on 30.06.2017.
 */
public class TemplateProcessorValidateTest {

    @InjectMocks
    private TemplateProcessor processor = new TemplateProcessorImpl();

    @Mock
    public MailTokenProviderImpl tokenProvider;

    @DataProvider
    private Object[][] mailTemplates() {
        return new Object[][]{

                // Correct templates
                {"", true},
                {"${token}", true},
                {" ${token}\n${token}", true},
                {"${token}${token}", true},
                {"${token} ${token}", true},

                // Correct templates with defaults
                {"${token | \"\"}", true},
                {"${token | \"another-value\"}", true},
                {"${token1 | \"another-value\"} ${token2 | \"test\"}", true},

                // Incorrect templates
                {"${", false},
                {"}", false},
                {"${${}", false},
                {"${}}", false},
                {"token}${token", false},
                {"${${token}}", false},
                {"${{token}}", false},

                // Incorrect templates with defaults
                {"${|}", false},
                {"${token|}", false},
                {"${token|123}", false},
                {"${token|\"}", false},
                {"${| \"123\"}", false},
                {"${| \"123\" | \"456\"}", false},
                {"${token1 | token2 | \"test\"}", false}
        };
    }

    @Test(dataProvider = "mailTemplates")
    public void validateSucceedWithResult(final String template, boolean expectedResult) {
        // given

        // when
        final boolean actualResult = processor.validate(template);

        // then
        assertThat(actualResult, is(expectedResult));
    }

    @BeforeMethod(alwaysRun = true)
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void validateThrowsWhenNullPassedAsParameter() {
        // given

        // when
        RuntimeException ex = null;
        try {
            processor.validate(null);
        } catch (RuntimeException e) {
            ex = e;
        }

        // then
        assertThat(ex, notNullValue());
    }
}
