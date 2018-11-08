package ru.glavkniga.gklients.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import ru.glavkniga.gklients.interfaces.FtpServerConfig;
import ru.glavkniga.gklients.mailing.EmlWriter;
import ru.glavkniga.gklients.mailing.MailMessage;
import ru.glavkniga.gklients.networking.FtpClient;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;
import static ru.glavkniga.gklients.service.MassMailingService.ErrorCode.*;

/**
 * Created by Vladimir on 12.06.2017.
 */
@Service(MassMailingService.NAME)
public class MassMailingServiceBean implements MassMailingService {

    private final static String LOG_FILE_ENCODING = "windows-1251";
    private final static String LOG_FILE_SESSION_PREFIX = "Session ";
    private final static String LOG_FILE_SESSION_SUFFIX = "; child";
    private final static String LOG_FILE_PREFIX = "./Logs/MDaemon-";
    private final static String LOG_FILE_SUFFIX = "-SMTP-(out).log";

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final static DateTimeFormatter LOG_FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Inject
    private FtpServerConfig ftpServerConfig;

    @Inject
    private EmlWriter writer;

    private FtpClient.FtpClientBuilder ftpClientBuilder;

    @PostConstruct
    private void init() {
        ftpClientBuilder = new FtpClient.FtpClientBuilder()
                .withConfiguration(ftpServerConfig);
    }

    @Override
    public void send(MailMessage message, List<String> receivers) throws MassMailingServiceException {

        if (isNull(message)) {
            throw new IllegalArgumentException("No message provided");
        }

        if (isEmpty(receivers)) {
            throw new IllegalArgumentException("No receivers provided");
        }

        final String date = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        final String receiversFilename = format("lists/%s_%s.csv", message.getId(), date);
        final String messageFilename = format("%s_%s.eml", message.getId(), date);

        try (final FtpClient ftpClient = ftpClientBuilder.build()) {
            connectFtpClientToServer(ftpClient);
            if (loginFtpClient(ftpClient)) {
                uploadReceiversToFtp(ftpClient, receiversFilename, receivers);
                uploadMailMessageTemplateToFtp(ftpClient, messageFilename, message);
            }
        }
    }

    @Override
    public Map<SentEmailInfo, Boolean> readStatistics(final LocalDate from) {
        Map<SentEmailInfo, Boolean> statuses = null;
        try (final FtpClient ftpClient = ftpClientBuilder.build()) {
            connectFtpClientToServer(ftpClient);
            if (loginFtpClient(ftpClient)) {
                final List<String> logs = retrieveLogFilenames(ftpClient);
                final List<String> actualLogs = filterAndSortLogFiles(logs, from);
                statuses = processLogFiles(ftpClient, actualLogs);

            }
        }
        return statuses;
    }

    private static List<String> retrieveLogFilenames(final FtpClient ftpClient) {
        try {
            return ftpClient.listFilesInFolder("./Logs");
        } catch (IOException ex) {
            throw new MassMailingServiceException(LIST_FTP_DIRECTORY_ERROR, ex);
        }
    }

    private static List<String> filterAndSortLogFiles(final List<String> logs, final LocalDate from) {
        if (logs == null) {
            throw new IllegalArgumentException("No log names passed to filterLogFiles");
        }

        final Map<LocalDate, String> unsortedFiles = new HashMap<>();

        for (String logFilename : logs) {
            if (logFilename == null || logFilename.isEmpty()) {
                continue;
            }

            if (!logFilename.startsWith(LOG_FILE_PREFIX) || !logFilename.endsWith(LOG_FILE_SUFFIX)) {
                continue;
            }

            logFilename = logFilename.trim();
            int prefixStart = logFilename.indexOf(LOG_FILE_PREFIX);
            int suffixStart = logFilename.indexOf(LOG_FILE_SUFFIX);
            final String dateFromName = logFilename.substring(prefixStart + LOG_FILE_PREFIX.length(), suffixStart);

            final LocalDate date = parseDate(dateFromName);
            if (date != null && (date.isEqual(from) || date.isAfter(from))) {
                unsortedFiles.put(date, logFilename);
            }

        }

        return unsortedFiles.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private static LocalDate parseDate(final String date) {
        try {
            return LocalDate.parse(date, LOG_FILENAME_FORMATTER);
        } catch (Exception ex) {
            return null;
        }
    }

    private static Map<SentEmailInfo, Boolean> processLogFiles(final FtpClient ftpClient, final List<String> actualLogs) {
        final Map<SentEmailInfo, Boolean> sentMailStatuses = new HashMap<>();
        for (final String logFileName : actualLogs) {
            File tempFile = null;
            FileOutputStream outputStream = null;
            try {
                tempFile = File.createTempFile("mdaemon", "log");
                outputStream = new FileOutputStream(tempFile);
                boolean result = ftpClient.downloadFile(logFileName, outputStream);
                if (result) {
                    analyzeLogFile(tempFile, sentMailStatuses);
                }
            } catch (IOException ex) {

                throw new MassMailingServiceException(IO_ERROR, ex);
            } finally {
                IOUtils.closeQuietly(outputStream);
                FileUtils.deleteQuietly(tempFile);
            }
        }
        return sentMailStatuses;
    }

    private static void analyzeLogFile(final File file, final Map<SentEmailInfo, Boolean> statuses) {
        InputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (Exception ex) {
            throw new MassMailingServiceException(IO_ERROR, ex);
        }

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(fis, LOG_FILE_ENCODING))) {
            String session = null;
            String prefixTo = null;
            String prefixSubject = null;
            String prefixSessionEnd = null;
            String to = null;
            String subject = null;
            Boolean delivered = false;
            for (; ; ) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.isEmpty()) {
                    continue;
                }
                if (session == null) {
                    final int start = line.indexOf(LOG_FILE_SESSION_PREFIX);
                    final int end = line.indexOf(LOG_FILE_SESSION_SUFFIX);
                    if (start > 0 && end > 0) {
                        session = line.substring(start + LOG_FILE_SESSION_PREFIX.length(), end);
                        prefixTo = "[" + session + "] *  To: ";
                        prefixSubject = "[" + session + "] *  Subject: ";
                        prefixSessionEnd = "[" + session + "] SMTP session ";
                    }
                    continue;
                }

                if (!session.isEmpty()) {

                    if (to == null) {
                        final int start = line.indexOf(prefixTo);
                        if (start > 0) {
                            to = line.substring(start + prefixTo.length()).trim();
                            continue;
                        }
                    }

                    if (subject == null) {
                        final int start = line.indexOf(prefixSubject);
                        if (start > 0) {
                            subject = line.substring(start + prefixSubject.length()).trim();
                            continue;
                        }
                    }

                    if (to != null && subject != null) {
                        final int start = line.indexOf(prefixSessionEnd);
                        if (start > 0) {
                            if (line.indexOf("SMTP session terminated") > 0) {
                                delivered = false;
                            } else if (line.indexOf("SMTP session successful") > 0) {
                                delivered = true;
                            }
                            if (delivered != null) {
                                statuses.put(new SentEmailInfo(to, subject), delivered);
                            }
                            to = null;
                            subject = null;
                            delivered = null;
                            prefixSessionEnd = null;
                            session = null;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new MassMailingServiceException(IO_ERROR, ex);
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    private void connectFtpClientToServer(final FtpClient ftpClient) {
        try {
            ftpClient.connect();
        } catch (final IOException ex) {
            throw new MassMailingServiceException(CONNECT_TO_FTP_SERVER_ERROR, ex);
        }
    }

    private boolean loginFtpClient(final FtpClient ftpClient) {
        try {
            return ftpClient.login();
        } catch (IOException ex) {
            throw new MassMailingServiceException(LOGIN_TO_FTP_SERVER_ERROR, ex);
        }
    }

    private void uploadReceiversToFtp(final FtpClient ftpClient, final String filename, final List<String> receivers) {
        final InputStream receiversStream = toInputStream(receivers);
        try {
            ftpClient.uploadFile(filename, receiversStream);
        } catch (final IOException ex) {
            throw new MassMailingServiceException(UPLOAD_RECEIVERS_LIST_ERROR, ex);
        }
    }

    private void uploadMailMessageTemplateToFtp(final FtpClient ftpClient, final String filename,
                                                final MailMessage message) {
        final InputStream messageStream = toInputStream(message);

        try {
            ftpClient.uploadFile(filename, messageStream);
        } catch (final IOException ex) {
            throw new MassMailingServiceException(UPLOAD_MAIL_MESSAGE_TEMPLATE_ERROR, ex);
        }
    }

    private InputStream toInputStream(final Collection<String> receivers) {
        final StringJoiner joiner = new StringJoiner("\n");
        for (final String receiver : receivers) {
            joiner.add(receiver);
        }
        return new ByteArrayInputStream(joiner.toString().getBytes(StandardCharsets.UTF_8));
    }

    private InputStream toInputStream(final MailMessage message) {
        return new ByteArrayInputStream(writer.toByteArray(message));
    }

}
