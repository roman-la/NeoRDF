package de.htw.ai.rest;

import de.htw.ai.App;
import de.htw.ai.models.NeoStatement;
import de.htw.ai.rdf.RdfConverter;
import de.htw.ai.rdf.RdfIO;
import de.htw.ai.rdf.SparqlConverter;
import org.eclipse.rdf4j.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.Collection;

@Path("/rest")
public class RestApi {

    private static Logger logger = LoggerFactory.getLogger(RestApi.class);
    private String[] validSyntax = new String[]{"BINARY", "JSONLS", "N3", "NQUADS", "NTRIPLES", "RDFJSON", "RDFXML", "TRIG", "TRIX", "TURTLE"};

    @GET
    @Path("/ontologies")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getOntologies() {
        return Response.status(Response.Status.OK).entity(App.ontologyHandler.ontologiesToString()).build();
    }

    @GET
    @Path("/extractRdf")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRdfData(@Context HttpHeaders headers) {
        if (headers.getHeaderString("format") == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("No format given.").build();

        if (!Arrays.stream(validSyntax).anyMatch(o -> o.equals(headers.getHeaderString("format"))))
            return Response.status(Response.Status.BAD_REQUEST).entity("Unknown format given.").build();

        Collection<NeoStatement> neoStatements = App.database.extractNeoStatements("MATCH (s)-[p]->(o) RETURN s, p, o;");
        Collection<Statement> rdf4jStatements = RdfConverter.neoStatementsToRdf4jStatements(neoStatements);
        String queryResult = RdfIO.statementsToString(rdf4jStatements, headers.getHeaderString("format"));

        return Response.status(Response.Status.OK).entity(queryResult).build();
    }

    @POST
    @Path("/cypher")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeCypherQuery(String query) {
        if (query == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("No query given.").build();

        String queryResult;

        try {
            queryResult = App.database.executeQuery(query).resultAsString();
        } catch (Exception e) {
            logger.error("", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error while processing query. Please refer to the log.").build();
        }

        return Response.status(Response.Status.OK).entity(queryResult).build();
    }

    @POST
    @Path("/sparql")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeSparqlQuery(String query) {
        if (query == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("No query given.").build();

        try {
            query = SparqlConverter.sparqlToCypher(query);
        } catch (Exception e) {
            logger.error("", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error converting sparql query. Please refer to the log.").build();
        }

        String queryResult;

        try {
            queryResult = App.database.executeQuery(query).resultAsString();
        } catch (Exception e) {
            logger.error("", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error while processing query. Please refer to the log.").build();
        }

        return Response.status(Response.Status.OK).entity(queryResult).build();
    }

    @POST
    @Path("/insertRdf")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertRdfData(String rdfData, @Context HttpHeaders headers) {
        if (rdfData == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("No data given.").build();

        if (headers.getHeaderString("format") == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("No format given.").build();

        Collection<Statement> rdf4jStatements;

        try {
            rdf4jStatements = RdfIO.stringToStatements(rdfData, headers.getHeaderString("format"));
        } catch (Exception e) {
            logger.error("", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error converting statements. Please refer to the log.").build();
        }

        if (rdf4jStatements == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Unknown format given.").build();

        Collection<NeoStatement> neoStatements;
        try {
            neoStatements = RdfConverter.rdf4jStatementsToNeoStatements(rdf4jStatements);
        } catch (Exception e) {
            logger.error("", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error converting statements. Please refer to the log.").build();
        }

        try {
            App.database.insertNeoStatements(neoStatements);
        } catch (Exception e) {
            logger.error("", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error while inserting data into database. Please refer to the log.").build();
        }

        return Response.status(Response.Status.OK).build();
    }
}
