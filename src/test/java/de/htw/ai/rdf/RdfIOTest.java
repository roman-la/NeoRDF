package de.htw.ai.rdf;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RdfIOTest {

    private static String rdfString;

    @BeforeAll
    public void beforeAll() throws IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource("rdfexample.txt").getPath());
        rdfString = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    @Test
    public void stringToStatementsTest() throws IOException {
        Collection<Statement> statements = RdfIO.stringToStatements(rdfString, RDFFormat.TURTLE);

        Assertions.assertEquals(9, statements.size());

        Assertions.assertTrue(statements.stream().anyMatch(o -> o.getSubject().stringValue().equals("http://example.org/#roman")));
    }

    @Test
    public void statementsToString() throws IOException {
        Collection<Statement> statements = RdfIO.stringToStatements(rdfString, RDFFormat.TURTLE);

        String rdfString = RdfIO.statementsToString(statements, RDFFormat.TURTLE);

        Assertions.assertTrue(rdfString.contains("<http://example.org/#green-goblin> a <http://xmlns.com/foaf/0.1/Person>;"));
        Assertions.assertTrue(rdfString.contains("<http://xmlns.com/foaf/0.1/name> \"Roman L.\" ."));
    }
}
