package de.htw.ai;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/rest")
public class RestApi {

    private Server jettyHttpServer;

    public RestApi() {
        jettyHttpServer = new Server(8080);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");
        jettyHttpServer.setHandler(contextHandler);
        ServletHolder jerseyServlet = contextHandler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        //jerseyServlet.setInitOrder(0); // maybe not needed, keep if problems occur
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

    @GET
    @Path("cypher")
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeCypherQuery(@QueryParam("query") String cypherQuery) {
        return Response.status(Response.Status.OK).entity(App.database.executeQuery(cypherQuery).resultAsString()).build();
    }
}
