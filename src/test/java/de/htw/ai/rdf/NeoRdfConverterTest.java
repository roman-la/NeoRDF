package de.htw.ai.rdf;

import de.htw.ai.App;
import de.htw.ai.models.*;
import de.htw.ai.util.NeoConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;

public class NeoRdfConverterTest {

    private static String ontologiesFilePath = new File("src/test/resources/tempontologies.txt").getAbsolutePath();

    @BeforeAll
    public static void beforeAll() {
        App.config = new NeoConfiguration();
        App.config.setConfigValue("ontologies", ontologiesFilePath);
        App.ontologyHandler = new OntologyHandler();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        Files.delete(Paths.get(ontologiesFilePath));
    }

    @Test
    public void stringToNeoStatementsTest() throws IOException {
        String rdfString = Files.readString(Paths.get(new File("src/test/resources/rdfexample.txt").getAbsolutePath()));

        Collection<NeoStatement> neoStatements = NeoRdfConverter.stringToNeoStatements(rdfString, "TURTLE");

        Assertions.assertNotNull(neoStatements);

        Assertions.assertEquals(3, neoStatements.size());
    }

    @Test
    public void NeoStatementsToString() {
        Collection<NeoStatement> neoStatements = new LinkedList<>() {{
            add(new NeoStatement(
                    new NeoIRI("http://example.org/#roman", "ex", "http://example.org/"),
                    new NeoIRI("http://example.org/type", "ex", "http://example.org/"),
                    new NeoIRI("http://example.org/Person", "ex", "http://example.org/")));
            add(new NeoStatement(
                    new NeoIRI("http://example.org/#roman", "ex", "http://example.org/"),
                    new NeoIRI("http://example.org/name", "ex", "http://example.org/"),
                    new NeoLiteral("Roman L.")));
        }};

        String rdfData = NeoRdfConverter.neoStatementsToString(neoStatements, "TURTLE");

        Assertions.assertNotNull(rdfData);

        String expected = "<http://example.org/#roman> <http://example.org/name> \"Roman L.\";\n" +
                "  <http://example.org/type> <http://example.org/Person> .";

        Assertions.assertTrue(rdfData.contains(expected));
    }
}
