package ru.glavkniga.gklients.service;

import com.google.common.collect.ImmutableList;
import com.haulmont.cuba.core.global.AppBeans;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import ru.glavkniga.gklients.BaseIT;
import ru.glavkniga.gklients.mailing.MailMessage;
import ru.glavkniga.gklients.networking.FtpClient;
import ru.glavkniga.gklients.rule.FtpRule;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static ru.glavkniga.gklients.mailing.Priority.HIGH;
import static ru.glavkniga.gklients.utils.TestUtils.loadProperties;

/**
 * Created by Vladimir on 14.06.2017.
 */
public class MassMailingServiceIT extends BaseIT {

    private static Properties properties = loadProperties("test-app.properties");

    @ClassRule
    public static FtpRule ftpRule = new FtpRule(getPathInTempFolder("ftpItRoot"), properties);

    private MassMailingService service;

    @Before
    public void setUp() {
        service = AppBeans.get(MassMailingService.class);
    }

    @Test
    public void sendSucceed() throws Exception {

        // given
        final String messageId = "message-id-123";

        final String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        final String receiversPath = format("lists/%s_%s.csv", messageId, date);
        final String messageTemplatePath = format("%s_%s.eml", messageId, date);

        final MailMessage message = new MailMessage()
                .withSubject("тема письма")
                .withBody("текст письма")
                .withMessageId(messageId)
                .withImportance(true)
                .withPriority(HIGH);

        final List<String> emails = ImmutableList.of(
                "sam@test.com",
                "bob@test.com",
                "rick@test.com",
                "tom@test.com"
        );

        final MailMessage mailMessage = new MailMessage()
                .withMessageId(messageId)
                .withSubject(message.getSubject())
                .withBody(message.getBody())
                .withImportance(message.isImportant());

        // when
        service.send(mailMessage, emails);

        // then
        final Message actualMessage = new MimeMessage(Session.getDefaultInstance(System.getProperties(), null),
                new ByteArrayInputStream(readFileContentFromFtp(messageTemplatePath, "test-receivers").getBytes()));

        final List<String> receiversList = asList(readFileContentFromFtp(receiversPath, "test-template").split("\n"));

        assertThat(actualMessage, notNullValue());
        assertThat(actualMessage.getSubject(), is("тема письма"));
        assertThat(actualMessage.getContent(), is("текст письма"));
        assertThat(actualMessage.getHeader("X-Distribution")[0], is(messageId));

        assertThat(receiversList, containsInAnyOrder(emails.toArray()));

    }


    @Test
    public void readStatisticsSucceed() throws Exception {
        // given
        final LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.YEARS);
        uploadFilesToFtp();

        // when
        final Map<MassMailingService.SentEmailInfo, Boolean> result = service.readStatistics(yesterday);

        // then
        assertThat(result, notNullValue());
    }

    private void uploadFilesToFtp() throws Exception {

        final List<String> files = Arrays.asList(
                "MDaemon-2017-07-02-SMTP-(out).log",
                "MDaemon-2017-07-03-SMTP-(out).log",
                "MDaemon-2017-07-06-SMTP-(out).log",
                "MDaemon-2017-07-05-SMTP-(out).log",
                "MDaemon-2017-07-04-SMTP-(out).log"
        );

        final FtpClient client = new FtpClient
                .FtpClientBuilder()
                .withHostAndPort("localhost", Integer.parseInt(properties.getProperty("gklients.core.ftp.port")))
                .withUserCredentials(
                        properties.getProperty("gklients.core.ftp.username"),
                        properties.getProperty("gklients.core.ftp.password"))
                .build()
                .connect();

        client.login();
        for (final String filename : files) {
            try (final InputStream is = this.getClass().getClassLoader().getResourceAsStream("testdata/" + filename)) {
                client.uploadFile("./Logs/" + filename, is);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

    }

    private String readFileContentFromFtp(final String path, final String filename) throws Exception {

        final File tempFile = Files.createTempFile(filename, ".dat").toFile();

        final FtpClient client = new FtpClient
                .FtpClientBuilder()
                .withHostAndPort("localhost", Integer.parseInt(properties.getProperty("gklients.core.ftp.port")))
                .withUserCredentials(
                        properties.getProperty("gklients.core.ftp.username"),
                        properties.getProperty("gklients.core.ftp.password"))
                .build()
                .connect();

        client.login();
        client.downloadFile(path, new FileOutputStream(tempFile));

        return new String(Files.readAllBytes(tempFile.toPath()));
    }
}
