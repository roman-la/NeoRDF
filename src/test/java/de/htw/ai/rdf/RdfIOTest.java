package de.htw.ai.rdf;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RdfIOTest {

    private static String rdfString;

    @BeforeAll
    public void beforeAll() throws IOException {
        rdfString = new String(Files.readAllBytes(Paths.get(new File("src/test/resources/rdfexample.txt").getAbsolutePath())), StandardCharsets.UTF_8);
    }

    @Test
    public void stringToStatementsTest() throws IOException {
        Collection<Statement> statements = RdfIO.stringToStatements(rdfString, "TURTLE");

        Assertions.assertNotNull(statements);

        Assertions.assertEquals(3, statements.size());

        Assertions.assertTrue(statements.stream().allMatch(o -> o.getSubject().stringValue().equals("http://example.org/#roman")));
    }

    @Test
    public void statementsToString() throws IOException {
        Collection<Statement> statements = RdfIO.stringToStatements(rdfString, "TURTLE");

        Assertions.assertNotNull(statements);

        String rdfString = RdfIO.statementsToString(statements, RDFFormat.TURTLE);

        String expected = "\n<http://example.org/#roman> <http://w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person>;\n" +
                "  <http://xmlns.com/foaf/0.1/name> \"Roman L.\";\n" +
                "  <http://xmlns.com/foaf/0.1/title> \"Mr\" .\n";

        Assertions.assertEquals(expected, rdfString);
    }
}
