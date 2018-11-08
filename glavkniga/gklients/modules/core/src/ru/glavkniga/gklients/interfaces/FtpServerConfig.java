package ru.glavkniga.gklients.interfaces;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;

/**
 * Created by Vladimir on 14.06.2017.
 */

@Source(type = SourceType.APP)
public interface FtpServerConfig extends Config {

    @Property("gklients.core.ftp.host")
    String getHost();

    @Property("gklients.core.ftp.port")
    Integer getPort();

    @Property("gklients.core.ftp.username")
    String getUsername();

    @Property("gklients.core.ftp.password")
    String getPassword();
}
