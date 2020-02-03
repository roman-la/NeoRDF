package de.htw.ai;

import de.htw.ai.db.GraphDatabase;
import de.htw.ai.models.NeoIRI;
import de.htw.ai.models.NeoLiteral;
import de.htw.ai.models.NeoStatement;
import de.htw.ai.rdf.OntologyHandler;
import de.htw.ai.rest.RestApi;
import de.htw.ai.util.Configuration;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestApiTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(RestApi.class);
    }

    @BeforeAll
    public void beforeAll() throws Exception {
        super.setUp(); // junit 5 with JerseyTest

        App.config = new Configuration();
        App.config.setConfigValue("dbdirectory", "src/test/resources/db");

        App.database = new GraphDatabase();

        App.database.insertNeoStatement(new NeoStatement(
                new NeoIRI("http://example.org/#roman", "ex", "http://example.org/"),
                new NeoIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
                new NeoIRI("http://xmlns.com/foaf/0.1/Person", "foaf", "http://xmlns.com/foaf/0.1/"))
        );

        App.database.insertNeoStatement(new NeoStatement(
                new NeoIRI("http://example.org/#roman", "ex", "http://example.org/"),
                new NeoIRI("http://xmlns.com/foaf/0.1/name", "foaf", "http://xmlns.com/foaf/0.1/"),
                new NeoLiteral("Roman L."))
        );
    }

    @AfterAll
    public void afterAll() throws Exception {
        super.tearDown(); // junit 5 with JerseyTest

        App.database.shutdown();

        FileUtils.deleteDirectory(new File("src/test/resources/db"));
    }

    @Test
    public void executeSparqlQueryWithDBTest() {
        String sparqlQuery = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "SELECT ?name\n" +
                "WHERE { ?x foaf:name ?name . }";

        Entity<String> cypherQueryEntity = Entity.entity(sparqlQuery, MediaType.TEXT_PLAIN);

        Response response = target("/rest/sparql").request().post(cypherQueryEntity);

        Assertions.assertEquals(200, response.getStatus());

        String responseString = response.readEntity(String.class);

        Assertions.assertTrue(responseString.contains("value:\"Roman L.\""));
        Assertions.assertTrue(responseString.contains("1 row"));
    }

    @Test
    public void executeCypherQueryWithDBTest() {
        String cypherQuery = "MATCH (n) RETURN n";

        Entity<String> cypherQueryEntity = Entity.entity(cypherQuery, MediaType.TEXT_PLAIN);

        Response response = target("/rest/cypher").request().post(cypherQueryEntity);

        Assertions.assertEquals(200, response.getStatus());

        String responseString = response.readEntity(String.class);

        Assertions.assertTrue(responseString.contains("iri:\"http://example.org/#roman\",ns:\"ex\",namespace:\"http://example.org/\""));
        Assertions.assertTrue(responseString.contains("iri:\"http://xmlns.com/foaf/0.1/Person\",ns:\"foaf\",namespace:\"http://xmlns.com/foaf/0.1/\""));
        Assertions.assertTrue(responseString.contains("value:\"Roman L.\""));
    }

    @Test
    public void getOntologiesTest() {
        App.config.setConfigValue("ontologies", new File("src/test/resources/ontologiesexample.txt").getAbsolutePath());
        App.ontologyHandler = new OntologyHandler();

        Response response = target("/rest/ontologies").request(MediaType.TEXT_PLAIN).get();

        Assertions.assertEquals(200, response.getStatus());

        String expectedString = "owl http://www.w3.org/2002/07/owl#\n" +
                "rdf http://www.w3.org/1999/02/22-rdf-syntax-ns#\n" +
                "rdfs http://www.w3.org/2000/01/rdf-schema#\n" +
                "foaf http://xmlns.com/foaf/0.1/";

        Assertions.assertEquals(expectedString, response.readEntity(String.class));
    }
}
