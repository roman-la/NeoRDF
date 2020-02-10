package de.htw.ai.rest;

import de.htw.ai.App;
import de.htw.ai.db.GraphDatabase;
import de.htw.ai.models.NeoIRI;
import de.htw.ai.models.NeoLiteral;
import de.htw.ai.models.NeoStatement;
import de.htw.ai.rdf.OntologyHandler;
import de.htw.ai.config.NeoConfiguration;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestApiTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(RestApi.class);
    }

    @BeforeAll
    public void beforeAll() throws Exception {
        super.setUp(); // junit 5 with JerseyTest

        // Setup config, ontologyhandler, graphdb
        App.config = new NeoConfiguration();
        App.config.setConfigValue("dbdir", "src/test/resources/db");
        App.config.setConfigValue("ontologies", new File("src/test/resources/ontologiesexample.txt").getAbsolutePath());
        App.ontologyHandler = new OntologyHandler();
        App.database = new GraphDatabase();
        App.database.start();

        // Add some data to db
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

        // Reset ontologyhandler
        String defaultContent = "owl http://www.w3.org/2002/07/owl#\n" +
                "rdf http://www.w3.org/1999/02/22-rdf-syntax-ns#\n" +
                "rdfs http://www.w3.org/2000/01/rdf-schema#\n" +
                "foaf http://xmlns.com/foaf/0.1/";

        Files.write(Paths.get(new File("src/test/resources/ontologiesexample.txt").getAbsolutePath()), defaultContent.getBytes());
    }

    @Test
    public void executeSparqlQueryTest() {
        String sparqlQuery = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "SELECT ?name\n" +
                "WHERE { ?x foaf:name ?name . }";

        Entity<String> cypherQueryEntity = Entity.entity(sparqlQuery, MediaType.TEXT_PLAIN);

        Response response = target("/rest/sparql").request().header("format", "table").post(cypherQueryEntity);

        Assertions.assertEquals(200, response.getStatus());

        String responseString = response.readEntity(String.class);

        System.out.println(responseString);

        Assertions.assertTrue(responseString.contains("value:\"Roman L.\""));
        Assertions.assertTrue(responseString.contains("1 row"));
    }

    @Test
    public void executeCypherQueryTest() {
        String cypherQuery = "MATCH (n) RETURN n";

        Entity<String> cypherQueryEntity = Entity.entity(cypherQuery, MediaType.TEXT_PLAIN);

        Response response = target("/rest/cypher").request().header("format", "table").post(cypherQueryEntity);

        Assertions.assertEquals(200, response.getStatus());

        String responseString = response.readEntity(String.class);

        Assertions.assertTrue(responseString.contains("iri:\"http://example.org/#roman\",ns:\"ex\",namespace:\"http://example.org/\""));
        Assertions.assertTrue(responseString.contains("iri:\"http://xmlns.com/foaf/0.1/Person\",ns:\"foaf\",namespace:\"http://xmlns.com/foaf/0.1/\""));
        Assertions.assertTrue(responseString.contains("value:\"Roman L.\""));
    }

    @Test
    public void getOntologiesTest() {
        Response response = target("/rest/ontologies").request(MediaType.TEXT_PLAIN).get();

        Assertions.assertEquals(200, response.getStatus());

        String responseString = response.readEntity(String.class);

        Collection<String> expectedLines = Arrays.asList("owl http://www.w3.org/2002/07/owl#",
                "rdf http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                "rdfs http://www.w3.org/2000/01/rdf-schema#",
                "foaf http://xmlns.com/foaf/0.1/");

        for (String expectedLine : expectedLines) {
            Assertions.assertTrue(responseString.contains(expectedLine));
        }
    }

    @Test
    public void extractRdfTest() {
        Response response = target("/rest/rdf").request(MediaType.TEXT_PLAIN).header("format", "TURTLE").get();

        Assertions.assertEquals(200, response.getStatus());

        String responseString = response.readEntity(String.class);

        String expected = "<http://example.org/#roman> a <http://xmlns.com/foaf/0.1/Person>;\n" +
                "  <http://xmlns.com/foaf/0.1/name> \"Roman L.\" .";

        Assertions.assertTrue(responseString.contains(expected));
    }
}
