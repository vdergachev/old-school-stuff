/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.networking;

/**
 * Created by vdergachev on 13.06.17.
 */

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.glavkniga.gklients.interfaces.FtpServerConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class FtpClient implements AutoCloseable {

    private static Logger log = LoggerFactory.getLogger(FtpClient.class);

    private String host;
    private int port;
    private String username;
    private String password;
    private final FTPClient client;
    private boolean authorized;

    private FtpClient() {
        client = new FTPClient();
        final FTPClientConfig config = new FTPClientConfig();
        config.setServerTimeZoneId("Europe/Moscow");
        client.configure(config);
        client.setDefaultTimeout(5000);
        client.setDataTimeout(5000);
    }

    private void setHost(final String host) {
        this.host = host;
    }

    private void setPort(final int port) {
        this.port = port;
    }

    protected void setUserCredentials(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAuthorized() {
        return client.isConnected() && authorized;
    }

    public FtpClient connect() throws IOException {

        if (client.isConnected()) {
            return this;
        }

        client.connect(host, port);

        if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
            final String message = "Can't connect to FTP Server \"" + host + ":" + port + "\"";
            log.warn(message);
        }

        return this;
    }

    public boolean login() throws IOException {

        authorized = client.login(username, password);

        if (!authorized || !FTPReply.isPositiveCompletion(client.getReplyCode())) {
            final String message =
                    format("Can't login to %s:%d with login %s and password %s", host, port, username, password);
            log.error(message);
        }
        client.setFileType(FTP.BINARY_FILE_TYPE);
        client.enterLocalPassiveMode();

        return authorized;
    }

    @Override
    public void close() {
        authorized = false;

        if (client.isConnected()) {
            try {
                client.logout();
                client.disconnect();
            } catch (final IOException ex) {
                log.warn("Error on logout/disconnect from ftp server");
            }
        }
    }

    public boolean uploadFile(final String path, final InputStream inputStream) throws IOException {

        if (!isAuthorized()) {
            throw new IllegalStateException("Upload file failed, client not connected to server");
        }

        return client.storeFile(path, inputStream);
    }

    public boolean downloadFile(final String filename, final OutputStream store) throws IOException {

        if (!isAuthorized()) {
            throw new IllegalStateException("Download file failed, client not connected to server");
        }

        return client.retrieveFile(filename, store);
    }

    public List<String> listFilesInFolder(final String logs) throws IOException {
        if (!isAuthorized()) {
            throw new IllegalStateException("listFilesInFolder failed, client not connected to server");
        }

        final String[] list = client.listNames(logs);
        return Arrays.asList(list);
    }

    public static class FtpClientBuilder {
        private String host;
        private int port;
        private String username;
        private String password;

        public FtpClientBuilder withHostAndPort(final String host, final int port) {
            this.host = host;
            this.port = port;
            return this;
        }

        public FtpClientBuilder withUserCredentials(final String username, final String password) {
            this.username = username;
            this.password = password;
            return this;
        }

        public FtpClientBuilder withConfiguration(final FtpServerConfig config) {
            host = config.getHost();
            port = config.getPort();
            username = config.getUsername();
            password = config.getPassword();
            return this;
        }

        public FtpClient build() {
            final FtpClient client = new FtpClient();
            client.setHost(host);
            client.setPort(port);
            client.setUserCredentials(username, password);
            return client;
        }

    }

}
