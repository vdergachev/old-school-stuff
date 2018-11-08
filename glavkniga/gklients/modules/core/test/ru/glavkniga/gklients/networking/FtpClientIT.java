/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.networking;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.util.SocketUtils;
import ru.glavkniga.gklients.rule.FtpRule;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Created by vdergachev on 13.06.17.
 */
public class FtpClientIT {

    private static final String ROOT = getPathInTempFolder("ftpItRoot");
    private final static String HOSTNAME = "localhost";
    private final static int PORT = SocketUtils.findAvailableTcpPort(20000);
    private final static String USERNAME = "testuser";
    private final static String PASSWORD = "test123";

    @Rule
    public FtpRule ftpRule = new FtpRule(ROOT, PORT, USERNAME, PASSWORD);

    private FtpClient client;

    @Before
    public void setUp() {
        client = new FtpClient.FtpClientBuilder()
                .withHostAndPort(HOSTNAME, PORT)
                .withUserCredentials(USERNAME, PASSWORD)
                .build();
    }

    @After
    public void tearDown() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void clientSuccessfullyConnected() throws Exception {

        // given

        // when
        final boolean result = client.connect().login();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void connectToServerWithWrongHostOrPortThrows() throws Exception {

        // given
        final FtpClient ftpClient = new FtpClient.FtpClientBuilder()
                .withHostAndPort(HOSTNAME + ".wrong", PORT + 1)
                .build();

        // when
        IOException ex = null;
        try {
            ftpClient.connect();
        } catch (IOException e) {
            ex = e;
        }

        // then
        assertThat(ex, notNullValue());
    }


    @Test
    public void connectToServerWithWrongCredentialsFails() throws Exception {

        // given
        client.setUserCredentials("wrong-name", "bad-pass-123");

        // when
        final boolean result = client.connect().login();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void uploadFailsWhenCredentialsIsWrong() throws Exception {

        // given
        client.setUserCredentials("wrong-name", "bad-pass-123");

        assertThat(client.connect().login(), is(false));

        // when
        Exception ex = null;
        try {
            client.uploadFile("test.txt", fromString("data"));
        } catch (Exception e) {
            ex = e;
        }

        // then
        assertThat(ex, notNullValue());
    }

    @Test
    public void clientSuccessfullyUploadFileToRootFolder() throws Exception {

        // given
        assertThat(client.connect().login(), is(true));

        // when
        final boolean result = client.uploadFile("test.txt", fromString("data"));

        // then
        assertThat(result, is(true));
    }

    @Test
    public void clientSuccessfullyUploadFileToSubFolder() throws Exception {

        // given
        assertThat(client.connect().login(), is(true));

        // when
        final boolean result = client.uploadFile("lists/test.txt", fromString("data"));

        // then
        assertThat(result, is(true));
    }

    @Test
    public void clientSuccessfullyOverwriteFileWhenUpload() throws Exception {

        // given
        assertThat(client.connect().login(), is(true));
        assertThat(client.uploadFile("test.txt", fromString("data")), is(true));

        // when
        final boolean result = client.uploadFile("test.txt", fromString("new data"));

        // then
        assertThat(result, is(true));
    }

    @Test
    public void uploadFileToNonExistingFolderFails() throws Exception {

        // given
        assertThat(client.connect().login(), is(true));

        // when
        final boolean result = client.uploadFile("nowhere/test.dat", fromString("test"));

        // then
        assertThat(result, is(false));
    }

    @Test
    public void clientSuccessfullyDownloadFileFromRootFolder() throws Exception {
        // given
        assertThat(client.connect().login(), is(true));
        assertThat(client.uploadFile("test.txt", fromString("upload test data")), is(true));

        final File tempFile = Files.createTempFile("downloaded-test", ".txt").toFile();
        final OutputStream store = new FileOutputStream(tempFile);

        // when
        final boolean result = client.downloadFile("test.txt", store);

        // then
        assertThat(result, is(true));
        assertThat(tempFile.exists(), is(true));

        final String actualFileContent = new String(Files.readAllBytes(tempFile.toPath()));

        assertThat(actualFileContent, is("upload test data"));
    }

    @Test
    public void clientSuccessfullyDownloadFileFromSubFolder() throws Exception {
        // given
        assertThat(client.connect().login(), is(true));
        assertThat(client.uploadFile("lists/test.txt", fromString("upload sub folder test data")), is(true));

        final File tempFile = Files.createTempFile("downloaded-test", ".txt").toFile();
        final OutputStream store = new FileOutputStream(tempFile);

        // when
        final boolean result = client.downloadFile("lists/test.txt", store);

        // then
        assertThat(result, is(true));
        assertThat(tempFile.exists(), is(true));

        final String actualFileContent = new String(Files.readAllBytes(tempFile.toPath()));

        assertThat(actualFileContent, is("upload sub folder test data"));
    }

    @Test
    public void clientSuccessfullyListFolder() throws Exception {
        // given
        assertThat(client.connect().login(), is(true));

        // when
        final List<String> files = client.listFilesInFolder("./Logs");

        // then
        assertThat(files, notNullValue());
        assertThat(files.size(), is(1));
    }

    // Bloody copy&paste from FtpRuleTest
    private InputStream fromString(final String value) {
        if (isNullOrEmpty(value)) {
            throw new IllegalArgumentException("No value passed to the method");
        }
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }

    // TODO Move to Testutils
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

}
