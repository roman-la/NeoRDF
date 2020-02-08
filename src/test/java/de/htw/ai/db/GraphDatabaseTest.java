package de.htw.ai.db;

import de.htw.ai.App;
import de.htw.ai.models.NeoIRI;
import de.htw.ai.models.NeoLiteral;
import de.htw.ai.models.NeoStatement;
import de.htw.ai.util.NeoConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.neo4j.graphdb.Result;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class GraphDatabaseTest {

    private static GraphDatabase db;

    @BeforeAll
    public static void beforeAll() {
        App.config = new NeoConfiguration();
        App.config.setConfigValue("dbdir", "src/test/resources/db");
    }

    @BeforeEach
    public void beforeEach() {
        db = new GraphDatabase();
        db.start();
    }

    @AfterEach
    public void afterEach() throws IOException {
        db.shutdown();

        FileUtils.deleteDirectory(new File("src/test/resources/db"));
    }

    @Test
    public void inputStatementTest() {
        db.insertNeoStatement(new NeoStatement(
                new NeoIRI("http://example.org/#roman", "ex", "http://example.org/"),
                new NeoIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
                new NeoIRI("http://xmlns.com/foaf/0.1/Person", "foaf", "http://xmlns.com/foaf/0.1/")));

        Assertions.assertTrue(db.executeQuery("MATCH (n) RETURN n;").resultAsString().contains("2 rows"));

        db.insertNeoStatement(new NeoStatement(
                new NeoIRI("http://example.org/#roman", "ex", "http://example.org/"),
                new NeoIRI("http://xmlns.com/foaf/0.1/name", "foaf", "http://xmlns.com/foaf/0.1/"),
                new NeoLiteral("Roman L.")));

        Assertions.assertTrue(db.executeQuery("MATCH (n) RETURN n;").resultAsString().contains("3 rows"));

        db.insertNeoStatement(new NeoStatement(
                new NeoIRI("http://example.org/#roman", "ex", "http://example.org/"),
                new NeoIRI("http://xmlns.com/foaf/0.1/name", "foaf", "http://xmlns.com/foaf/0.1/"),
                new NeoLiteral("Roman L.")));

        Assertions.assertTrue(db.executeQuery("MATCH (n) RETURN n;").resultAsString().contains("3 rows"));

        String resultSubject = db.executeQuery("MATCH (n {iri: \"http://example.org/#roman\", ns: \"ex\", namespace: \"http://example.org/\"}) RETURN n;").resultAsString();

        Assertions.assertTrue(resultSubject.contains("1 row"));
    }

    @Test
    public void restartDatabaseTest() {
        db.insertNeoStatement(new NeoStatement(
                new NeoIRI("http://example.org/#roman", "ex", "http://example.org/"),
                new NeoIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
                new NeoIRI("http://xmlns.com/foaf/0.1/Person", "foaf", "http://xmlns.com/foaf/0.1/")));

        Assertions.assertTrue(db.executeQuery("MATCH (n) RETURN n;").resultAsString().contains("2 rows"));

        db.shutdown();

        db.start();

        Assertions.assertTrue(db.executeQuery("MATCH (n) RETURN n;").resultAsString().contains("2 rows"));

        db.shutdown();

        db = new GraphDatabase();

        db.start();

        Assertions.assertTrue(db.executeQuery("MATCH (n) RETURN n;").resultAsString().contains("2 rows"));
    }

    @Test
    public void extractNeoStatementsTest() {
        db.insertNeoStatement(new NeoStatement(
                new NeoIRI("http://example.org/#roman", "ex", "http://example.org/"),
                new NeoIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
                new NeoIRI("http://xmlns.com/foaf/0.1/Person", "foaf", "http://xmlns.com/foaf/0.1/")));

        db.insertNeoStatement(new NeoStatement(
                new NeoIRI("http://example.org/#roman", "ex", "http://example.org/"),
                new NeoIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
                new NeoLiteral("test")));

        Collection<NeoStatement> statements = db.extractNeoStatements("MATCH (s)-[p]->(o) RETURN s, p, o;");

        for (NeoStatement statement : statements) {
            NeoIRI subject = (NeoIRI) statement.getSubject();
            NeoIRI predicate = statement.getPredicate();

            Assertions.assertEquals("http://example.org/#roman", subject.getProperties().get("iri"));
            Assertions.assertEquals("rdf", predicate.getProperties().get("ns"));

            if (statement.getObject() instanceof NeoIRI) {
                predicate = statement.getPredicate();
                Assertions.assertEquals("rdf", predicate.getProperties().get("ns"));
            } else if (statement.getObject() instanceof NeoLiteral) {
                NeoLiteral literal = (NeoLiteral) statement.getObject();
                Assertions.assertEquals("test", literal.getValue());
            }
        }
    }

    @Test
    public void executeQueryTest() {
        db.insertNeoStatement(new NeoStatement(new NeoIRI("http://example.org/#roman", "ex", "http://example.org/"),
                new NeoIRI("http://xmlns.com/foaf/0.1/name", "foaf", "http://xmlns.com/foaf/0.1/"),
                new NeoLiteral("Roman L."))
        );

        Result r = db.executeQuery("MATCH (x)-[:predicate {iri: \"http://xmlns.com/foaf/0.1/name\"}]->(name)\nRETURN name");

        String resultString = r.resultAsString();

        Assertions.assertTrue(resultString.contains("{value:\"Roman L.\"}"));
        Assertions.assertTrue(resultString.contains("1 row"));
    }
}
