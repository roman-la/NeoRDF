package de.htw.ai.rest;

import de.htw.ai.App;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HttpServer {

    private Server jettyHttpServer;

    public HttpServer() {
        jettyHttpServer = new Server(Integer.parseInt(App.config.getConfigValue("port")));

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        contextHandler.setContextPath("/");

        jettyHttpServer.setHandler(contextHandler);

        ServletHolder jerseyServlet = contextHandler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");

        jerseyServlet.setInitOrder(0); // maybe not needed, keep if problems occur

        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", RestApi.class.getCanonicalName());
    }

    public void start() {
        try {
            jettyHttpServer.start();
            jettyHttpServer.join();
        } catch (Exception e) {
            stop();
        }
    }

    public void stop() {
        try {
            jettyHttpServer.stop();
            jettyHttpServer.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
