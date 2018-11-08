package ru.glavkniga.gklients.interfaces;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;

/**
 * Created by Vladimir on 14.06.2017.
 */
@Source(type = SourceType.APP)
public interface MailingTemplateConfig extends Config {
    @Property("gklients.core.mailing.template.complaints")
    String getComplaints();

    @Property("gklients.core.mailing.template.sender")
    String getSender();

    @Property("gklients.core.mailing.template.reply")
    String getReplyTo();

    @Property("gklients.core.mailing.template.errors")
    String getErrors();
}
