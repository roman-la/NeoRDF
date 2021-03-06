package de.htw.ai.rest;

import de.htw.ai.App;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing the integrated jetty http server
 */
public class HttpServer {

    private Server jettyHttpServer;
    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public HttpServer() {
        jettyHttpServer = new Server(Integer.parseInt(App.config.getConfigValue("port")));

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        contextHandler.setContextPath("/");

        jettyHttpServer.setHandler(contextHandler);

        ServletHolder jerseyServlet = contextHandler.addServlet(ServletContainer.class, "/*");

        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", RestApi.class.getCanonicalName());
    }

    public void start() throws Exception {
        logger.info("Starting embedded jetty http web server");

        jettyHttpServer.start();
        jettyHttpServer.join();
    }

    public void stop() {
        logger.info("Stopping embedded jetty http web server");

        try {
            jettyHttpServer.stop();
        } catch (Exception e) {
            logger.error("", e);
        }
        jettyHttpServer.destroy();
    }
}
