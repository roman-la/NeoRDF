package de.htw.ai.rest;

import de.htw.ai.App;
import de.htw.ai.models.NeoStatement;
import de.htw.ai.rdf.RdfConverter;
import de.htw.ai.rdf.RdfIO;
import de.htw.ai.rdf.SparqlConverter;
import org.eclipse.rdf4j.model.Statement;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.Collection;

@Path("/rest")
public class RestApi {

    @GET
    @Path("/ontologies")
    @Produces({MediaType.TEXT_PLAIN})
    public Response getOntologies() {
        return Response.status(Response.Status.OK).entity(App.ontologyHandler.ontologiesToString()).build();
    }

    @POST
    @Path("/cypher")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeCypherQuery(String query) {
        if (query == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("No data given.").build();

        String queryResult;

        try {
            queryResult = App.database.executeQuery(query).resultAsString();
        } catch (Exception e) {
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
            return Response.status(Response.Status.BAD_REQUEST).entity("No data given.").build();

        String convertedQuery;
        try {
            convertedQuery = SparqlConverter.sparqlToCypher(query);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error converting sparql query. Please refer to the log.").build();
        }

        String resultString;
        try {
            resultString = App.database.executeQuery(convertedQuery).resultAsString();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error while processing query. Please refer to the log.").build();
        }

        return Response.status(Response.Status.OK).entity(resultString).build();
    }

    @POST
    @Path("/rdf")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertRdfData(String rdfData, @Context HttpHeaders headers) throws IOException {
        if (rdfData == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("No data given.").build();

        if (headers.getHeaderString("format") == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("No format given.").build();

        Collection<Statement> rdf4jStatements;

        try {
            rdf4jStatements = RdfIO.stringToStatements(rdfData, headers.getHeaderString("format"));
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error converting statements. Please refer to the log.").build();
        }

        if (rdf4jStatements == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Unknown format given.").build();

        Collection<NeoStatement> neoStatements;
        try {
            neoStatements = RdfConverter.rdf4jStatementsToNeoStatements(rdf4jStatements);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error converting statements. Please refer to the log.").build();
        }

        try {
            App.database.insertNeoStatements(neoStatements);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error while inserting data into database. Please refer to the log.").build();
        }

        return Response.status(Response.Status.OK).build();
    }
}
