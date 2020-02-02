package de.htw.ai.db;

import de.htw.ai.App;
import de.htw.ai.models.NeoElement;
import de.htw.ai.models.NeoIRI;
import de.htw.ai.models.NeoLiteral;
import de.htw.ai.models.NeoStatement;
import de.htw.ai.util.Configuration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Entity;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class GraphDatabaseTest {

    private static GraphDatabase db;

    @BeforeAll
    static void beforeAll() {
        App.config = new Configuration();
        App.config.setConfigValue("dbdirectory", "src/test/resources/db");
    }

    @AfterAll
    static void afterAll() throws IOException {
        db.shutdown();

        FileUtils.deleteDirectory(new File("src/test/resources/db"));
    }

    @Test
    public void inputStatementTest() {
        NeoIRI subject = new NeoIRI("http://example.org/#roman", "ex", "http://example.org/");
        NeoIRI predicate1 = new NeoIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        NeoIRI object1 = new NeoIRI("http://xmlns.com/foaf/0.1/Person", "foaf", "http://xmlns.com/foaf/0.1/");
        NeoStatement neoStatement1 = new NeoStatement(subject, predicate1, object1);

        db = new GraphDatabase();

        db.insertNeoStatement(neoStatement1);
        String countTest = db.executeQuery("MATCH (n) RETURN n;").resultAsString();
        Assertions.assertTrue(countTest.contains("2 rows"));

        NeoIRI predicate2 = new NeoIRI("http://xmlns.com/foaf/0.1/name", "foaf", "http://xmlns.com/foaf/0.1/");
        NeoLiteral object2 = new NeoLiteral("Roman L.");
        NeoStatement neoStatement2 = new NeoStatement(subject, predicate2, object2);

        db.insertNeoStatement(neoStatement2);
        countTest = db.executeQuery("MATCH (n) RETURN n;").resultAsString();
        Assertions.assertTrue(countTest.contains("3 rows"));

        db.insertNeoStatement(neoStatement2);
        countTest = db.executeQuery("MATCH (n) RETURN n;").resultAsString();
        Assertions.assertTrue(countTest.contains("3 rows"));

        String resultSubject = db.executeQuery("MATCH (n:subject {iri: \"http://example.org/#roman\", ns: \"ex\", namespace: \"http://example.org/\"}) RETURN n;").resultAsString();
        Assertions.assertTrue(resultSubject.contains("1 row"));
    }

    @Test
    public void restartDatabaseTest() {
        NeoIRI subject = new NeoIRI("http://example.org/#roman", "ex", "http://example.org/");
        NeoIRI predicate1 = new NeoIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        NeoIRI object1 = new NeoIRI("http://xmlns.com/foaf/0.1/Person", "foaf", "http://xmlns.com/foaf/0.1/");
        NeoStatement neoStatement1 = new NeoStatement(subject, predicate1, object1);

        db = new GraphDatabase();

        db.insertNeoStatement(neoStatement1);
        String countTest = db.executeQuery("MATCH (n) RETURN n;").resultAsString();
        Assertions.assertTrue(countTest.contains("2 rows"));

        db.shutdown();

        db = new GraphDatabase();

        countTest = db.executeQuery("MATCH (n) RETURN n;").resultAsString();
        Assertions.assertTrue(countTest.contains("2 rows"));
    }

    @Test
    public void extractNeoStatementsTest() {
        NeoIRI subject = new NeoIRI("http://example.org/#roman", "ex", "http://example.org/");
        NeoIRI predicate = new NeoIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        NeoIRI object = new NeoIRI("http://xmlns.com/foaf/0.1/Person", "foaf", "http://xmlns.com/foaf/0.1/");
        NeoLiteral literal = new NeoLiteral("test");

        db = new GraphDatabase();

        db.insertNeoStatement(new NeoStatement(subject, predicate, object));
        db.insertNeoStatement(new NeoStatement(subject, predicate, literal));

        Collection<NeoStatement> statements = db.extractNeoStatements("MATCH (s)-[p]->(o) RETURN s, p, o;");

        for (NeoStatement statement : statements) {
            subject = (NeoIRI) statement.getSubject();
            predicate = (NeoIRI) statement.getPredicate();

            Assertions.assertEquals("http://example.org/#roman", subject.getProperties().get("iri"));
            Assertions.assertEquals("rdf", predicate.getProperties().get("ns"));

            if (statement.getObject() instanceof NeoIRI) {
                predicate = (NeoIRI) statement.getPredicate();
                Assertions.assertEquals("rdf", predicate.getProperties().get("ns"));
            } else if (statement.getObject() instanceof NeoLiteral) {
                literal = (NeoLiteral) statement.getObject();
                Assertions.assertEquals("test", literal.getValue());
            }
        }
    }
}
