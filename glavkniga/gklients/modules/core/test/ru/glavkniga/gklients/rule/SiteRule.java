package ru.glavkniga.gklients.rule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpStatus;
import org.junit.rules.ExternalResource;
import ru.glavkniga.gklients.fake.FakeWebServer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by Vladimir on 28.05.2017.
 */
public class SiteRule extends ExternalResource {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String CONTEXT = "/app";

    private final String host;
    private final int port;
    private FakeWebServer fakeWebServer;

    public final AttachmentUploadCapturingServlet uploadCapturingServlet;

    public SiteRule(String host, int port) {
        this.uploadCapturingServlet = new AttachmentUploadCapturingServlet();
        this.host = host;
        this.port = port;
    }

    @Override
    protected void before() throws Throwable {
        fakeWebServer = new FakeWebServer(CONTEXT, host, port)
                .withServlet(new OkSendingServlet("1"), "/add")
                .withServlet(new LoginServlet(), "/login")
                .withServlet(uploadCapturingServlet, "/upload")
                .start();
    }

    @Override
    protected void after() {
        stop();
    }

    public void stop() {
        fakeWebServer.stop();
    }

    public static class OkSendingServlet extends HttpServlet {

        private final String message;

        public OkSendingServlet(final String message) {
            this.message = message;
        }

        @Override
        protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
                throws ServletException, IOException {
            if (!Strings.isNullOrEmpty(message)) {
                response.getWriter().write(message);
                response.getWriter().flush();
            }
            response.setStatus(HttpStatus.SC_OK);
        }
    }

    public static class LoginServlet extends HttpServlet {
        @Override
        protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
                throws ServletException, IOException {
            response.setContentType("application/json");
            OBJECT_MAPPER.writeValue(response.getWriter(), ImmutableMap.of(
                    "session_id", UUID.randomUUID().toString()
            ));
            response.getWriter().flush();
            response.setStatus(HttpStatus.SC_OK);
        }
    }

    public static class AttachmentUploadCapturingServlet extends HttpServlet {

        private int numberOfFiles;
        private String filename;
        private byte[] content;

        private boolean fail = false;

        @Override
        protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
                throws ServletException, IOException {

            final List<FileItem> fileItems = getFileItems(request);

            numberOfFiles = fileItems.size();
            filename = fileItems.get(0).getName();
            content = IOUtils.toByteArray(fileItems.get(0).getInputStream());

            if(fail) {
                response.setStatus(HttpStatus.SC_NOT_FOUND);
                return;
            }

            response.setContentType("application/json");
            OBJECT_MAPPER.writeValue(response.getWriter(), ImmutableMap.of(
                    "path", "http://localhost/upload/attachment/" + UUID.randomUUID().toString() + ".jpg"
            ));
            response.getWriter().flush();
            response.setStatus(HttpStatus.SC_OK);
        }

        private List<FileItem> getFileItems(final HttpServletRequest request) {
            final ServletContext servletContext = this.getServletConfig().getServletContext();
            final File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");

            final DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setRepository(repository);

            final ServletFileUpload upload = new ServletFileUpload(factory);
            try {
                return upload.parseRequest(request);
            } catch (FileUploadException e) {
                return null;
            }
        }

        public void setFail(final boolean fail) {
            this.fail = fail;
        }

        public int getNumberOfFiles() {
            return numberOfFiles;
        }

        public String getFilename() {
            return filename;
        }

        public byte[] getContent() {
            return content;
        }
    }


}
