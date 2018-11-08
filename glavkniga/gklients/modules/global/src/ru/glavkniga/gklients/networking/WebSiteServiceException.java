package ru.glavkniga.gklients.networking;

/**
 * Created by vdergachev on 03.07.17.
 */
public class WebSiteServiceException extends RuntimeException {

    private final WebSiteServiceError error;

    public WebSiteServiceException(final WebSiteServiceError error) {
        this.error = error;
    }

    public WebSiteServiceException(final WebSiteServiceError error, final Throwable cause) {
        super(cause);
        this.error = error;
    }

    public WebSiteServiceError getError() {
        return error;
    }
}
