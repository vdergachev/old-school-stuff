package ru.glavkniga.gklients.networking;

import com.haulmont.cuba.core.global.AppBeans;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import ru.glavkniga.gklients.BaseIT;
import ru.glavkniga.gklients.rule.SiteRule;
import ru.glavkniga.gklients.service.SiteFileUploadService;

import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by vdergachev on 03.07.17.
 */
public class SiteFileUploadServiceIT extends BaseIT {

    @ClassRule
    public static SiteRule server = new SiteRule("localhost", 38888);

    private SiteFileUploadService serviceClient;

    private static final byte[] FILE_CONTENT = new byte[]{1, 2, 3};
    private static final String FILENAME = "test.txt";

    @Before
    public void setup() {
        serviceClient = AppBeans.get(SiteFileUploadService.class);
    }

    @Test
    public void clientUploadsFileSuccessfully() {
        // given

        // when
        final URL url = serviceClient.uploadAttachment(FILE_CONTENT, FILENAME);

        // then
        assertThat(url, notNullValue());

        assertThat(server.uploadCapturingServlet.getContent(), is(FILE_CONTENT));
        assertThat(server.uploadCapturingServlet.getNumberOfFiles(), is(1));
        assertThat(server.uploadCapturingServlet.getFilename(), is(FILENAME));
    }

    @Test
    public void clientThrowsOnNotFoundStatusCode() {
        // given
        server.uploadCapturingServlet.setFail(true);

        // when
        WebSiteServiceException ex = null;
        try {
            serviceClient.uploadAttachment(FILE_CONTENT, FILENAME);
        }catch (WebSiteServiceException e) {
            ex = e;
        }

        // then
        assertThat(ex, notNullValue());
        assertThat(ex.getError(), is(WebSiteServiceError.FILE_UPLOAD_FAILURE));
    }

    @Test
    public void clientThrowsWhenConnectionTimeLapsed() {
        // given
        server.stop();

        // when
        WebSiteServiceException ex = null;
        try {
            serviceClient.uploadAttachment(FILE_CONTENT, FILENAME);
        }catch (WebSiteServiceException e) {
            ex = e;
        }

        // then
        assertThat(ex, notNullValue());
        assertThat(ex.getError(), is(WebSiteServiceError.FILE_UPLOAD_FAILURE));
    }

    // TODO Add wrong JSON as response

    // TODO Add case with 5XX response code

}
