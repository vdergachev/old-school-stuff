package ru.glavkniga.gklients.rule.test;

import com.google.common.base.Strings;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.util.SocketUtils;
import ru.glavkniga.gklients.rule.FtpRule;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;

/**
 * Created by Vladimir on 29.05.2017.
 */

//TODO Restore ftp filesystem after eqach test
public class FtpRuleTest {

    private static final int PORT = SocketUtils.findAvailableTcpPort();
    private static final String HOST = "localhost";
    private static final String ROOT = getPathInTempFolder("ftpRoot");
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @ClassRule
    public static FtpRule ftpRule = new FtpRule(ROOT, PORT, USERNAME, PASSWORD);

    private FTPClient client = new FTPClient();

    @Before
    public void initClient() throws Exception {
        client = new FTPClient();
    }

    @After
    public void tearDown() throws Exception {
        if (client.isConnected()) {
            client.logout();
            client.disconnect();
        }
    }

    @Test
    public void loginSucceed() throws Exception {
        // given
        givenConnectedToServerFtpClient();

        // when
        boolean result = client.login(USERNAME, PASSWORD);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void loginFailsWithWrongCredentials() throws Exception {
        // given
        givenConnectedToServerFtpClient();

        // when
        boolean result = client.login("test", "123");

        // then
        assertThat(result, is(false));
    }

    @Test
    public void uploadAndDownloadFileToServerSucceed() throws Exception {
        // given
        givenReadyFtpClient();

        // when
        boolean stored = client.storeFile("lists/test.csv", fromString("test file content"));

        // then
        boolean retrieved;
        final String downloadPath = getPathInTempFolder("test.csv");
        try (final OutputStream os = new BufferedOutputStream(new FileOutputStream(downloadPath))) {
            retrieved = client.retrieveFile("lists/test.csv", os);
        }

        final String fileContent = readFileToString(new File(downloadPath), StandardCharsets.UTF_8);

        assertThat(stored, is(true));
        assertThat(retrieved, is(true));
        assertThat(fileExists(downloadPath), is(true));
        assertThat(fileContent, is("test file content"));
    }


    @Test
    public void storeFileToNotExistingFolderFails() throws Exception {
        // given
        givenReadyFtpClient();
        final String downloadPath = getPathInTempFolder("test.csv");

        // when
        boolean stored = client.storeFile("noname/test.csv", fromString("test file content"));

        // then
        boolean retrieved;
        try (final OutputStream os = new BufferedOutputStream(new FileOutputStream(downloadPath))) {
            retrieved = client.retrieveFile("lists/test.csv", os);
        }

        final String fileContent = readFileToString(new File(downloadPath), StandardCharsets.UTF_8);

        assertThat(stored, is(false));
        assertThat(retrieved, is(false));
        assertThat(fileContent, isEmptyOrNullString());
    }

    @Test
    public void retrieveFileFromServerFailsWhenFileDoesNotExist() throws Exception {
        // given
        givenReadyFtpClient();
        final String downloadPath = getPathInTempFolder("noname.csv");

        // when
        boolean retrieved;
        try (final OutputStream os = new BufferedOutputStream(new FileOutputStream(downloadPath))) {
            retrieved = client.retrieveFile("lists/noname.txt", os);
        }

        // then
        final String fileContent = readFileToString(new File(downloadPath), StandardCharsets.UTF_8);

        assertThat(retrieved, is(false));
        assertThat(fileContent, isEmptyOrNullString());
    }

    private void givenConnectedToServerFtpClient() throws Exception {
        final FTPClientConfig config = new FTPClientConfig();
        config.setServerTimeZoneId("Europe/Moscow");
        client.configure(config);
        client.connect(HOST, PORT);
        final int reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            client.disconnect();
            throw new RuntimeException("Exception in connecting to FTP Server");
        }
    }

    private void givenReadyFtpClient() throws Exception {
        givenConnectedToServerFtpClient();
        client.login(USERNAME, PASSWORD);
        int reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            client.disconnect();
            throw new RuntimeException("Exception in connecting to FTP Server");
        }
        client.setFileType(FTP.ASCII_FILE_TYPE);
        client.enterLocalPassiveMode();
        reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            client.disconnect();
            throw new RuntimeException("Exception in connecting to FTP Server");
        }
    }

    private InputStream fromString(final String value) {
        if (isNullOrEmpty(value)) {
            throw new IllegalArgumentException("No value passed to the method");
        }
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }

    // TODO Move to FileUtils
    private static String getPathInTempFolder(final String... folders) {
        if (folders.length == 0) {
            throw new IllegalArgumentException("No folders passed to the method");
        }
        final String tmpPath = System.getProperty("java.io.tmpdir");
        if (isNullOrEmpty(tmpPath)) {
            throw new IllegalStateException("Can't get temporary folder path");
        }
        return Paths.get(Paths.get(tmpPath).toString(), folders).toString();
    }

    private static boolean fileExists(final String path) {
        if (Strings.isNullOrEmpty(path)) {
            throw new IllegalArgumentException("Path is null or empty");
        }
        return new File(path).exists();
    }

}
