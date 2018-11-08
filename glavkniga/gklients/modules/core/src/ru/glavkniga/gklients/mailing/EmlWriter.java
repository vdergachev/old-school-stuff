package ru.glavkniga.gklients.mailing;

import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import ru.glavkniga.gklients.interfaces.MailingTemplateConfig;

import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static javax.mail.Message.RecipientType.*;

/**
 * Created by Vladimir on 12.06.2017.
 */
@Component
public class EmlWriter {

    public final static String HEADER_PRIORITY = "X-Priority";
    public final static String HEADER_ID = "Message-ID";
    public final static String HEADER_SENDER = "Sender";
    public final static String HEADER_X_SENDER = "X-Sender";
    public final static String HEADER_UNSUBSCRIBE = "List-Unsubscribe";
    public final static String HEADER_X_COMPLAINTS = "X-Complaints-To";
    public final static String HEADER_REPLY = "Reply-To";
    public final static String HEADER_X_DISTRIBUTION = "X-Distribution";
    public final static String HEADER_RETURN_PATH = "Return-Path";
    public final static String HEADER_X_ERRORS_TO = "X-Errors-To";
    public final static String HEADER_ERRORS_TO = "Errors-To";
    public final static String HEADER_IMPORTANCE = "Importance";

    @Inject
    private MailingTemplateConfig config;

    @Inject
    private FileStorageAPI fileStorageAPI;

    public EmlWriter(final MailingTemplateConfig config) {
        this.config = config;
    }

    public byte[] toByteArray(final MailMessage mailMessage) {
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream(2048)) {
            final Message message = createMimeMessage(mailMessage);
            message.writeTo(output);
            return output.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Can't serialize mail message", ex.getCause());
        }
    }

    private Message createMimeMessage(final MailMessage mailMessage) throws Exception {

        final Message message = createMessageWithId(mailMessage.getId());

        setHeaders(message, mailMessage);

        setRecipients(message, TO, mailMessage.getTo());
        setRecipients(message, CC, mailMessage.getCc());
        setRecipients(message, BCC, mailMessage.getBcc());

        message.setFrom(isNullOrEmpty(mailMessage.getFrom()) ? null : new InternetAddress(mailMessage.getFrom()));
        message.setSubject(mailMessage.getSubject());

        setBodyAndAttachments(message, mailMessage);

        return message;
    }

    private void setBodyAndAttachments(final Message message, final MailMessage mailMessage) throws Exception {

        final List<FileDescriptor> attachments = mailMessage.getAttachments();
        if (attachments == null || attachments.size() == 0) {
            message.setContent(mailMessage.getBody(), "text/html; charset=utf-8");
            return;
        }

        final Multipart multipart = new MimeMultipart();
        message.setContent(multipart);

        // create the message part
        final MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setHeader("Content-Type", "text/html; charset=\"utf-8\"");
        bodyPart.setContent(mailMessage.getBody(), "text/html; charset=utf-8");
        bodyPart.setHeader("Content-Transfer-Encoding", "base64"); //setHeader("Content-Transfer-Encoding", "quoted-printable");
        multipart.addBodyPart(bodyPart);

        // add attachments
        for (final FileDescriptor fileDescriptor : attachments) {
            final MimeBodyPart attachment = new MimeBodyPart();
            attachment.setDataHandler(createDataHandler(fileDescriptor));
            attachment.setHeader("Content-Transfer-Encoding", "base64");
            attachment.setFileName(fileDescriptor.getName());
            multipart.addBodyPart(attachment);
        }

    }

    private void setRecipients(final Message message, final Message.RecipientType type, final String value)
            throws MessagingException {
        message.setRecipients(type, isNullOrEmpty(value) ? null : InternetAddress.parse(value));
    }

    private void setHeaders(final Message message, final MailMessage mailMessage) throws MessagingException {
        // Headers setup
        message.setHeader(HEADER_X_COMPLAINTS, config.getComplaints());
        message.setHeader(HEADER_SENDER, config.getSender());
        message.setHeader(HEADER_X_SENDER, config.getSender());
        message.setHeader(HEADER_REPLY, config.getReplyTo());
        message.setHeader(HEADER_RETURN_PATH, config.getErrors());
        message.setHeader(HEADER_X_ERRORS_TO, config.getErrors());
        message.setHeader(HEADER_ERRORS_TO, config.getErrors());
        message.setHeader(HEADER_X_DISTRIBUTION, mailMessage.getId());
        message.setHeader(HEADER_PRIORITY, mailMessage.getPriority().getAsString());
        message.setHeader(HEADER_IMPORTANCE, mailMessage.isImportant() ? "High" : "");
        message.setHeader(HEADER_UNSUBSCRIBE, isNullOrEmpty(mailMessage.getUnsubscribeUrl()) ? "" :
                mailMessage.getUnsubscribeUrl());
    }

    private Message createMessageWithId(final String id) {
        return new MimeMessage(Session.getInstance(System.getProperties())) {
            @Override
            protected void updateMessageID() throws MessagingException {
                setHeader(HEADER_ID, id);
            }
        };
    }

    public DataHandler createDataHandler(final FileDescriptor fileDescriptor) throws Exception {
        final byte[] fileContent = fileStorageAPI.loadFile(fileDescriptor);
        final AutoDetectParser parser = new AutoDetectParser();
        final BodyContentHandler handler = new BodyContentHandler();
        final Metadata metadata = new Metadata();
        parser.parse(new ByteArrayInputStream(fileContent), handler, metadata);
        final String fileType = metadata.get("Content-Type");
        return new DataHandler(new ByteArrayDataSource(fileContent, fileType));
    }
}
