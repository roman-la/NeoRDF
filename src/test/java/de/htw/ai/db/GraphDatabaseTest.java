package de.htw.ai.db;

import de.htw.ai.App;
import de.htw.ai.models.NeoIRI;
import de.htw.ai.models.NeoLiteral;
import de.htw.ai.models.NeoStatement;
import de.htw.ai.util.Configuration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class GraphDatabaseTest {

    @BeforeAll
    static void beforeAll() {
        App.config = new Configuration();
        App.config.setConfigValue("dbdirectory", "src/test/resources/db");
    }

    @AfterAll
    static void afterAll() throws IOException {
        FileUtils.deleteDirectory(new File("/src/test/resources/db"));
    }

    @Test
    public void inputStatementTest() {
        NeoIRI subject = new NeoIRI("http://example.org/#roman", "ex", "http://example.org/");
        NeoIRI predicate1 = new NeoIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        NeoIRI object1 = new NeoIRI("http://xmlns.com/foaf/0.1/Person", "foaf", "http://xmlns.com/foaf/0.1/");
        NeoStatement neoStatement1 = new NeoStatement(subject, predicate1, object1);

        GraphDatabase db = new GraphDatabase();

        db.inputStatement(neoStatement1);

        NeoIRI predicate2 = new NeoIRI("http://xmlns.com/foaf/0.1/name", "foaf", "http://xmlns.com/foaf/0.1/");
        NeoLiteral object2 = new NeoLiteral("Roman L.");
        NeoStatement neoStatement2 = new NeoStatement(subject, predicate2, object2);

        db.inputStatement(neoStatement2);
    }
}
