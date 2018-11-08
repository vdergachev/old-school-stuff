package ru.glavkniga.gklients.fake;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SocketUtils;

import javax.servlet.Servlet;

import static java.lang.String.format;

/**
 * Created by Vladimir on 26.05.2017.
 */

public class FakeWebServer {

    private Logger log = LoggerFactory.getLogger(FakeWebServer.class);

    private Server server;
    private ServletContextHandler contextHandler;
    private final int port;

    public FakeWebServer(final String context, final String host, int port) {
        this.port = port == 0 ? SocketUtils.findAvailableTcpPort() : port;

        server = new Server();

        final ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        connector.setHost(host);
        server.setConnectors(new Connector[]{connector});

        contextHandler = new ServletContextHandler();
        contextHandler.setContextPath(context);
        server.setHandler(contextHandler);
    }

    public FakeWebServer withServlet(final Servlet servlet, final String mapping) {
        if (!server.isRunning()) {
            contextHandler.addServlet(new ServletHolder(servlet), mapping);
        }
        return this;
    }

    public FakeWebServer start() {
        if (!server.isRunning()) {
            try {
                server.start();
            } catch (final Exception ex) {
                log.error(format("Can't start jetty server on %d port", getPort()), ex);
            }
        }
        return this;
    }

    public void stop() {
        if (server.isRunning()) {
            try {
                server.stop();
            } catch (final Exception ex) {
                log.error(format("Can't stop jetty server on %d port", getPort()), ex);
            }
        }
    }

    public int getPort() {
        return port;
    }

}
