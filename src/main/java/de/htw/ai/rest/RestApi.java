package de.htw.ai.rest;

import de.htw.ai.App;
import de.htw.ai.models.NeoStatement;
import de.htw.ai.rdf.RdfConverter;
import de.htw.ai.rdf.RdfIO;
import de.htw.ai.rdf.SparqlConverter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collection;

@Path("/rest")
public class RestApi {

    private Server jettyHttpServer;

    public RestApi() {
        jettyHttpServer = new Server(Integer.parseInt(App.config.getConfigValue("port")));

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
    @Path("/cypher")
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeCypherQuery(@QueryParam("query") String query) {
        return Response.status(Response.Status.OK).entity(App.database.executeQuery(query).resultAsString()).build();
    }

    @GET
    @Path("/sparql")
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeSparqlQuery(@QueryParam("query") String query) {
        String convertedQuery = SparqlConverter.sparqlToCypher(query);

        String resultString = App.database.executeQuery(convertedQuery).resultAsString();

        return Response.status(Response.Status.OK).entity(resultString).build();
    }

    @POST
    @Path("/rdf")
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertRdfData(String rdfData, @QueryParam("format") String format) throws IOException {
        Collection<Statement> rdf4jStatements = RdfIO.stringToStatements(rdfData, format);
        Collection<NeoStatement> neoStatements = RdfConverter.rdf4jStatementsToNeoStatements(rdf4jStatements);
        App.database.insertNeoStatements(neoStatements);

        return Response.status(Response.Status.OK).build();
    }
}
