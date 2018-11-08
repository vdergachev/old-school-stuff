package ru.glavkniga.gklients.mailing;

import com.haulmont.cuba.core.entity.FileDescriptor;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Vladimir on 12.06.2017.
 */

public class MailMessage implements Serializable {

    private static final long serialVersionUID = 6588409654280063197L;

    private String to;
    private String from;
    private String subject;
    private String body;
    private String bcc;
    private String cc;
    private String id;
    private String unsubscribeUrl;
    private boolean important;
    private Priority priority = Priority.NORMAL;
    private List<FileDescriptor> attachments;

    public MailMessage() {
    }

    public MailMessage withMessageId(final String id) {
        this.id = id;
        return this;
    }

    public MailMessage withTo(final String to) {
        this.to = to;
        return this;
    }

    public MailMessage withFrom(final String from) {
        this.from = from;
        return this;
    }

    public MailMessage withSubject(final String subject) {
        this.subject = subject;
        return this;
    }

    public MailMessage withBody(final String body) {
        this.body = body;
        return this;
    }

    public MailMessage withBcc(final String bcc) {
        this.bcc = bcc;
        return this;
    }

    public MailMessage withCc(final String cc) {
        this.cc = cc;
        return this;
    }

    public MailMessage withPriority(final Priority priority) {
        this.priority = priority;
        return this;
    }

    public MailMessage withAttachments(final List<FileDescriptor> attachments) {
        this.attachments = attachments;
        return this;
    }

    public MailMessage withImportance(final boolean important) {
        this.important = important;
        return this;
    }

    public MailMessage withUnsubscribeUrl(final String unsubscribeUrl) {
        this.unsubscribeUrl = unsubscribeUrl;
        return this;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getBcc() {
        return bcc;
    }

    public String getCc() {
        return cc;
    }

    public Priority getPriority() {
        return priority;
    }

    public List<FileDescriptor> getAttachments() {
        return attachments;
    }

    public String getId() {
        return id;
    }

    public boolean isImportant() {
        return important;
    }

    public String getUnsubscribeUrl() {
        return unsubscribeUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MailMessage message = (MailMessage) o;

        if (important != message.important) return false;
        if (to != null ? !to.equals(message.to) : message.to != null) return false;
        if (from != null ? !from.equals(message.from) : message.from != null) return false;
        if (subject != null ? !subject.equals(message.subject) : message.subject != null) return false;
        if (body != null ? !body.equals(message.body) : message.body != null) return false;
        if (bcc != null ? !bcc.equals(message.bcc) : message.bcc != null) return false;
        if (cc != null ? !cc.equals(message.cc) : message.cc != null) return false;
        if (id != null ? !id.equals(message.id) : message.id != null) return false;
        if (unsubscribeUrl != null ? !unsubscribeUrl.equals(message.unsubscribeUrl) : message.unsubscribeUrl != null)
            return false;
        return priority == message.priority;
    }

    @Override
    public String toString() {
        return "MailMessage{" +
                "to='" + to + '\'' +
                ", from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", bcc='" + bcc + '\'' +
                ", cc='" + cc + '\'' +
                ", id='" + id + '\'' +
                ", unsubscribeUrl='" + unsubscribeUrl + '\'' +
                ", important=" + important +
                ", priority=" + priority +
                '}';
    }

}
