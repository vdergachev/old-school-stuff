package ru.glavkniga.gklients.rule.test;

import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import ru.glavkniga.gklients.rule.MailRule;

import javax.mail.internet.MimeMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by Vladimir on 29.05.2017.
 */

// TODO https://github.com/greenmail-mail-test/greenmail/blob/master/greenmail-standalone/src/test/java/com/icegreen/greenmail/standalone/GreenMailStandaloneRunnerTest.java

public class MailRuleTest {

    @ClassRule
    public static MailRule mailRule = new MailRule();

    @After
    public void tearDown() {
        mailRule.cleanMailBox();
    }

    @Test
    public void mailSuccessfullySentToServer() {
        // given

        // when
        GreenMailUtil.sendTextEmail("subscribers@subscribers.glavkniga.ru",
                "errors@subscribers.glavkniga.ru",
                "subject",
                "message",
                ServerSetupTest.SMTP);

        // then
        final boolean isReceived = mailRule.waitForIncomingEmail(5000, 1);
        final MimeMessage[] receivedMessages = mailRule.getReceivedMessages();

        assertThat(isReceived, is(true));
        assertThat(receivedMessages, notNullValue());
        assertThat(receivedMessages.length, is(1));

        final String messageBody = GreenMailUtil.getBody(receivedMessages[0]);

        assertThat(messageBody, is("message"));

    }


}
