package ru.glavkniga.gklients.mailing;

import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.glavkniga.gklients.interfaces.MailingTemplateConfig;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.mockito.Mockito.when;
import static ru.glavkniga.gklients.mailing.EmlWriter.*;

/**
 * Created by Vladimir on 12.06.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmlWriterTest {

    private final static String MAIL_TEMPLATE_SUBJECT = "Тема нашего письма";
    private final static String MAIL_TEMPLATE_BODY = "Содержимое письма";
    private final static String MAIL_TEMPLATE_MESSAGE_ID = "<message-id@localhost.test>";
    private final static String MAIL_TEMPLATE_CC = "cc@localhost.test";
    private final static String MAIL_TEMPLATE_BCC = "bcc@localhost.test";
    private final static String MAIL_TEMPLATE_UNSUBSCRIBE_URL = "http://unsubscribe.localhost.test";
    private final static String MAIL_TEMPLATE_COMPLAINTS = "complaints@localhost";
    private final static String MAIL_TEMPLATE_SENDER = "sender@localhost";
    private final static String MAIL_TEMPLATE_REPLYTO = "replyme@localhost";
    private final static String MAIL_TEMPLATE_ERRORS = "errors@localhost";

    @InjectMocks
    private final EmlWriter writer = new EmlWriter(givenTemplateConfig());

    @Mock
    private FileStorageAPI fileStorageAPI;

    @Test
    public void writePlainMessageSucceed() throws Exception {

        // given
        final MailMessage mailMessage = givenMailMessage();

        // when
        final byte[] result = writer.toByteArray(mailMessage);

        // then
        final MimeMessage message = new MimeMessage(Session.getDefaultInstance(System.getProperties(), null),
                new ByteArrayInputStream(result));

        assertThat(message.getFrom(), hasAddressInArray(MAIL_TEMPLATE_SENDER));
        assertThat(message.getReplyTo(), hasAddressInArray(MAIL_TEMPLATE_REPLYTO));
        assertThat(message.getSubject(), is(MAIL_TEMPLATE_SUBJECT));
        assertThat(message.getContent(), is(MAIL_TEMPLATE_BODY));
        assertThat(message.getMessageID(), is(MAIL_TEMPLATE_MESSAGE_ID));

        final Address[] ccRecipients = message.getRecipients(Message.RecipientType.CC);
        final Address[] bccRecipients = message.getRecipients(Message.RecipientType.BCC);
        final Priority priority = Priority.parse(message.getHeader(HEADER_PRIORITY)[0]);
        final String unsubscribeUrl = message.getHeader(HEADER_UNSUBSCRIBE)[0];
        final String messageId = message.getHeader(HEADER_ID)[0];
        final String sender = message.getHeader(HEADER_SENDER)[0];
        final String xSender = message.getHeader(HEADER_X_SENDER)[0];
        final String complaints = message.getHeader(HEADER_X_COMPLAINTS)[0];
        final String replyTo = message.getHeader(HEADER_REPLY)[0];
        final String distribution = message.getHeader(HEADER_X_DISTRIBUTION)[0];
        final String returnPath = message.getHeader(HEADER_RETURN_PATH)[0];
        final String xErrors = message.getHeader(HEADER_X_ERRORS_TO)[0];
        final String errorsTo = message.getHeader(HEADER_ERRORS_TO)[0];
        final String importance = message.getHeader(HEADER_IMPORTANCE)[0];

        assertThat(priority, is(Priority.HIGH));
        assertThat(ccRecipients, hasAddressInArray(MAIL_TEMPLATE_CC));
        assertThat(bccRecipients, hasAddressInArray(MAIL_TEMPLATE_BCC));
        assertThat(unsubscribeUrl, is(MAIL_TEMPLATE_UNSUBSCRIBE_URL));
        assertThat(messageId, is(MAIL_TEMPLATE_MESSAGE_ID));
        assertThat(sender, is(MAIL_TEMPLATE_SENDER));
        assertThat(xSender, is(MAIL_TEMPLATE_SENDER));
        assertThat(complaints, is(MAIL_TEMPLATE_COMPLAINTS));
        assertThat(replyTo, is(MAIL_TEMPLATE_REPLYTO));
        assertThat(distribution, is(MAIL_TEMPLATE_MESSAGE_ID));
        assertThat(returnPath, is(MAIL_TEMPLATE_ERRORS));
        assertThat(xErrors, is(MAIL_TEMPLATE_ERRORS));
        assertThat(errorsTo, is(MAIL_TEMPLATE_ERRORS));
        assertThat(importance, is("High"));
    }

    @Test
    public void writeMessageWithAttachmentSucceed() throws Exception {
        // given
        final List<FileDescriptor> attachments = givenFilesToAttach();
        final MailMessage mailMessage = givenMailMessage()
                .withAttachments(attachments);

        // when
        final byte[] result = writer.toByteArray(mailMessage);

        // then
        final MimeMessage message = new MimeMessage(Session.getDefaultInstance(System.getProperties(), null),
                new ByteArrayInputStream(result));

        assertThat(message.getContentType(), containsString("multipart"));

        final Multipart multiPart = (Multipart) message.getContent();
        assertThat(multiPart.getCount(), is(attachments.size() + 1));

        final String actualBody = multiPart.getBodyPart(0).getContent().toString();

//        final String firstFileContent = multiPart.getBodyPart(1).getContent().toString();
//        final String firstFileName = multiPart.getBodyPart(1).getFileName();
//        final String firstFileContentType = multiPart.getBodyPart(1).getContentType();
//
//        final String secondFileContent = multiPart.getBodyPart(2).getContent().toString();
//        final String secondFileName = multiPart.getBodyPart(2).getFileName();
//        final String secondFileContentType = multiPart.getBodyPart(2).getContentType();

//        final String thirdFileContent = multiPart.getBodyPart(3).getContent().toString();
//        final String thirdFileName = multiPart.getBodyPart(3).getFileName();
//        final String thirdFileContentType = multiPart.getBodyPart(3).getContentType();

        assertThat(actualBody, is(MAIL_TEMPLATE_BODY));

//        assertThat(firstFileName, is("firstFile.txt"));
//        assertThat(firstFileContent, is("первый файл молодец"));
//        assertThat(firstFileContentType, containsString("text/plain; charset=UTF-8"));
//
//        assertThat(secondFileName, is("второйфайл.txt"));
//        assertThat(secondFileContent, is("second file data"));
//        assertThat(secondFileContentType, containsString("text/plain; charset=UTF-8"));
//
//        assertThat(thirdFileContent, is("ну тоже ниче так"));
//        assertThat(thirdFileName, is("третий.txt"));
//        assertThat(thirdFileContentType, containsString("text/plain; charset=UTF-8"));
    }

    // TODO Add tests with failing cases
    private List<FileDescriptor> givenFilesToAttach() throws Exception {
        return asList(
                createFileWithContent("firstFile.txt", "первый файл молодец"),
                createFileWithContent("второйфайл.txt", "second file data"),
                createFileWithContent("третий.txt", "ну тоже ниче так")
        );
    }

    private FileDescriptor createFileWithContent(final String filename, final String content) throws Exception {
        final FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setName(filename);
        when(fileStorageAPI.loadFile(fileDescriptor)).thenReturn(content.getBytes());
        return fileDescriptor;
    }

    private MailMessage givenMailMessage() {
        return new MailMessage()
                .withMessageId(MAIL_TEMPLATE_MESSAGE_ID)
                .withSubject(MAIL_TEMPLATE_SUBJECT)
                .withBody(MAIL_TEMPLATE_BODY)
                .withCc(MAIL_TEMPLATE_CC)
                .withBcc(MAIL_TEMPLATE_BCC)
                .withPriority(Priority.HIGH)
                .withImportance(true)
                .withUnsubscribeUrl(MAIL_TEMPLATE_UNSUBSCRIBE_URL);

    }

    private MailingTemplateConfig givenTemplateConfig() {
        return new MailingTemplateConfig() {
            @Override
            public String getComplaints() {
                return MAIL_TEMPLATE_COMPLAINTS;
            }

            @Override
            public String getSender() {
                return MAIL_TEMPLATE_SENDER;
            }

            @Override
            public String getReplyTo() {
                return MAIL_TEMPLATE_REPLYTO;
            }

            @Override
            public String getErrors() {
                return MAIL_TEMPLATE_ERRORS;
            }
        };
    }

    private static Matcher<Address[]> hasAddressInArray(final String address) throws AddressException {
        return arrayContainingInAnyOrder(InternetAddress.parse(address));
    }
}
