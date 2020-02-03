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
            return Response.status(Response.Status.BAD_REQUEST).build();

        return Response.status(Response.Status.OK).entity(App.database.executeQuery(query).resultAsString()).build();
    }

    @POST
    @Path("/sparql")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeSparqlQuery(String query) {
        if (query == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        String convertedQuery = SparqlConverter.sparqlToCypher(query);

        String resultString = App.database.executeQuery(convertedQuery).resultAsString();

        return Response.status(Response.Status.OK).entity(resultString).build();
    }

    @POST
    @Path("/rdf")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertRdfData(String rdfData, @Context HttpHeaders headers) throws IOException {
        Collection<Statement> rdf4jStatements = RdfIO.stringToStatements(rdfData, headers.getHeaderString("format"));
        Collection<NeoStatement> neoStatements = RdfConverter.rdf4jStatementsToNeoStatements(rdf4jStatements);

        App.database.insertNeoStatements(neoStatements);

        return Response.status(Response.Status.OK).build();
    }
}
