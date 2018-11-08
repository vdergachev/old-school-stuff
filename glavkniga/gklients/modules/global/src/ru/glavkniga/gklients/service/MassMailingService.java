package ru.glavkniga.gklients.service;

import ru.glavkniga.gklients.mailing.MailMessage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by Vladimir on 12.06.2017.
 */
public interface MassMailingService {

    String NAME = "gklients_MassMailingService";

    void send(MailMessage message, List<String> receivers) throws MassMailingServiceException;

    Map<SentEmailInfo, Boolean> readStatistics(LocalDate from);

    enum ErrorCode {
        CONNECT_TO_FTP_SERVER_ERROR,
        LOGIN_TO_FTP_SERVER_ERROR,
        UPLOAD_RECEIVERS_LIST_ERROR,
        UPLOAD_MAIL_MESSAGE_TEMPLATE_ERROR,
        LIST_FTP_DIRECTORY_ERROR,
        IO_ERROR
    }

    class MassMailingServiceException extends RuntimeException {

        private final ErrorCode errorCode;

        MassMailingServiceException(final ErrorCode code, final Throwable cause) {
            super(cause);
            this.errorCode = code;
        }

        public ErrorCode getErrorCode() {
            return errorCode;
        }
    }

    class SentEmailInfo {
        private final String email;
        private final String subject;

        public String getEmail() {
            return email;
        }

        public String getSubject() {
            return subject;
        }

        public SentEmailInfo(final String email, final String subject) {
            this.email = email;
            this.subject = subject;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final SentEmailInfo that = (SentEmailInfo) o;

            if (email != null ? !email.equals(that.email) : that.email != null) return false;
            return subject != null ? subject.equals(that.subject) : that.subject == null;

        }

        @Override
        public int hashCode() {
            int result = email != null ? email.hashCode() : 0;
            result = 31 * result + (subject != null ? subject.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "SentEmailInfo{" +
                    "email='" + email + '\'' +
                    ", subject='" + subject + '\'' +
                    '}';
        }
    }

}
