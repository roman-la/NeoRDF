package de.htw.ai.db;

import de.htw.ai.App;
import de.htw.ai.models.NeoIRI;
import de.htw.ai.models.NeoLiteral;
import de.htw.ai.models.NeoStatement;
import de.htw.ai.util.Configuration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

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

        db.inputStatement(neoStatement1);
        String countTest = db.executeQuery("MATCH (n) RETURN n;").resultAsString();
        Assertions.assertTrue(countTest.contains("2 rows"));

        NeoIRI predicate2 = new NeoIRI("http://xmlns.com/foaf/0.1/name", "foaf", "http://xmlns.com/foaf/0.1/");
        NeoLiteral object2 = new NeoLiteral("Roman L.");
        NeoStatement neoStatement2 = new NeoStatement(subject, predicate2, object2);

        db.inputStatement(neoStatement2);
        countTest = db.executeQuery("MATCH (n) RETURN n;").resultAsString();
        Assertions.assertTrue(countTest.contains("3 rows"));

        db.inputStatement(neoStatement2);
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

        db.inputStatement(neoStatement1);
        String countTest = db.executeQuery("MATCH (n) RETURN n;").resultAsString();
        Assertions.assertTrue(countTest.contains("2 rows"));

        db.shutdown();

        db = new GraphDatabase();

        countTest = db.executeQuery("MATCH (n) RETURN n;").resultAsString();
        Assertions.assertTrue(countTest.contains("2 rows"));
    }
}
