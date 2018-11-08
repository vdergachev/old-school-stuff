package ru.glavkniga.gklients.rule;

import org.junit.rules.ExternalResource;
import ru.glavkniga.gklients.fake.FakeFtpServer;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by Vladimir on 29.05.2017.
 */
public class FtpRule extends ExternalResource {

    // All path in unix like style by default
    private final static List<String> SERVER_FOLDERS = Arrays.asList(
            "lists",
            "archive",
            "Logs",
            "Logs/OldLogs"
    );

    private final String root;
    private final int port;
    private final String username;
    private final String password;

    private FakeFtpServer fakeFtpServer;

    public FtpRule(String root, final Properties properties) {
        this.root = root;
        this.port = Integer.parseInt(properties.getProperty("gklients.core.ftp.port"));
        this.username = properties.getProperty("gklients.core.ftp.username");
        this.password = properties.getProperty("gklients.core.ftp.password");
    }

    public FtpRule(String root, int port, String username, String password) {
        this.root = root;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    @Override
    protected void before() {
        fakeFtpServer = new FakeFtpServer(port)
                .withFileSystem(root, SERVER_FOLDERS)
                .withUser(username, password)
                .start();
    }

    @Override
    protected void after() {
        fakeFtpServer.stop();
    }
}
