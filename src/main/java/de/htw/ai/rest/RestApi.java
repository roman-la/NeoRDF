package de.htw.ai.rest;

import de.htw.ai.App;
import de.htw.ai.models.NeoStatement;
import de.htw.ai.rdf.RdfConverter;
import de.htw.ai.rdf.SparqlConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Collection;

@Path("/rest")
public class RestApi {

    private static Logger logger = LoggerFactory.getLogger(RestApi.class);

    @GET
    @Path("/ontologies")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getOntologies() {
        return Response.status(Response.Status.OK).entity(App.ontologyHandler.ontologiesToString()).build();
    }

    @GET
    @Path("/rdf")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRdfData(@Context HttpHeaders headers) {
        if (headers.getHeaderString("format") == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("No format given.").build();

        Collection<NeoStatement> neoStatements;
        try {
            neoStatements = App.database.extractNeoStatements("MATCH (s)-[p]->(o) RETURN s, p, o;");
        } catch (Exception e) {
            logger.error("", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error while querying result from database. Please refer to the log.").build();
        }

        String queryResult;
        try {
            queryResult = RdfConverter.neoStatementsToString(neoStatements, headers.getHeaderString("format"));
        } catch (IllegalArgumentException ie) {
            logger.error("", ie);
            return Response.status(Response.Status.BAD_REQUEST).entity("Unknown format given.").build();
        } catch (Exception e) {
            logger.error("", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error while converting database result to string. Please refer to the log.").build();
        }

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
    @Path("/rdf")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertRdfData(String rdfData, @Context HttpHeaders headers) {
        if (rdfData == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("No data given.").build();

        if (headers.getHeaderString("format") == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("No format given.").build();

        Collection<NeoStatement> neoStatements;
        try {
            neoStatements = RdfConverter.stringToNeoStatements(rdfData, headers.getHeaderString("format"));
        } catch (IllegalArgumentException ie) {
            logger.error("", ie);
            return Response.status(Response.Status.BAD_REQUEST).entity("Unknown format given.").build();
        } catch (Exception e) {
            logger.error("", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error while converting rdf string. Please refer to the log.").build();
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
