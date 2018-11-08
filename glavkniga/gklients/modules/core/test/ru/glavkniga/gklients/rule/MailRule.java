package ru.glavkniga.gklients.rule;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.rules.ExternalResource;

import javax.mail.internet.MimeMessage;

/**
 * Created by Vladimir on 26.05.2017.
 */

// https://stackoverflow.com/questions/8167583/create-an-email-object-in-java-and-save-it-to-file

public class MailRule extends ExternalResource {

    private final GreenMail greenMail;

    public MailRule() {
        greenMail = new GreenMail(ServerSetupTest.SMTP_POP3);
    }

    @Override
    protected void before() {
        greenMail.setUser("subscribers@subscribers.glavkniga.ru", "subscribers", "37gd7rsdg3");
        greenMail.setUser("errors@subscribers.glavkniga.ru", "errors", "2309Dk23");
        greenMail.start();

    }

    @Override
    protected void after() {
        greenMail.stop();
    }

    public MimeMessage[] getReceivedMessages() {
        return greenMail.getReceivedMessages();
    }

    public boolean waitForIncomingEmail(final long timeout, final int count) {
        return greenMail.waitForIncomingEmail(timeout, count);
    }

    public void cleanMailBox() {
        try {
            greenMail.purgeEmailFromAllMailboxes();
        }catch (FolderException ex) {
            throw new RuntimeException("Can't purge messages from mailbox", ex);
        }
    }
}
