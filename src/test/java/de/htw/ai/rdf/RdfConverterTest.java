package de.htw.ai.rdf;

import de.htw.ai.App;
import de.htw.ai.models.*;
import de.htw.ai.util.NeoConfiguration;
import org.eclipse.rdf4j.model.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class RdfConverterTest {

    @BeforeAll
    public static void beforeAll() {
        App.config = new NeoConfiguration();
        App.config.setConfigValue("ontologies", new File("src/test/resources/ontologiesexample.txt").getAbsolutePath());
    }

    @BeforeAll
    public static void beforeEach() {
        App.ontologyHandler = new OntologyHandler();
    }

    @AfterEach
    public void afterEach() throws IOException {
        String defaultContent = "owl http://www.w3.org/2002/07/owl#\n" +
                "rdf http://www.w3.org/1999/02/22-rdf-syntax-ns#\n" +
                "rdfs http://www.w3.org/2000/01/rdf-schema#\n" +
                "foaf http://xmlns.com/foaf/0.1/";

        Files.write(Paths.get(new File("src/test/resources/ontologiesexample.txt").getAbsolutePath()), defaultContent.getBytes());
    }

    @Test
    public void rdf4jStatementToNeoStatementTest() throws IOException {
        String rdfString = new String(
                Files.readAllBytes(Paths.get(new File("src/test/resources/rdfexample.txt").getAbsolutePath())),
                StandardCharsets.UTF_8);

        Collection<Statement> rdf4jStatements = RdfIO.stringToStatements(rdfString, "TURTLE");

        Assertions.assertNotNull(rdf4jStatements);

        Collection<NeoStatement> neoStatements = RdfConverter.rdf4jStatementsToNeoStatements(rdf4jStatements);

        Assertions.assertEquals(3, neoStatements.size());

        for (NeoStatement s : neoStatements) {
            if (s.getSubject() instanceof NeoIRI)
                Assertions.assertEquals("http://example.org/#roman", ((NeoIRI) s.getSubject()).getProperties().get("iri"));

            Assertions.assertTrue(
                    s.getPredicate().getProperties().get("iri").equals("http://w3.org/1999/02/22-rdf-syntax-ns#type") ||
                            s.getPredicate().getProperties().get("iri").equals("http://xmlns.com/foaf/0.1/title") ||
                            s.getPredicate().getProperties().get("iri").equals("http://xmlns.com/foaf/0.1/name"));

            if (s.getObject() instanceof NeoIRI) {
                Assertions.assertEquals("http://xmlns.com/foaf/0.1/Person", ((NeoIRI) s.getObject()).getProperties().get("iri"));
            } else if (s.getObject() instanceof NeoLiteral) {
                Assertions.assertTrue(((NeoLiteral) s.getObject()).getValue().equals("Mr") || ((NeoLiteral) s.getObject()).getValue().equals("Roman L."));
            }
        }
    }

    @Test
    public void rdf4jStatementToNeoStatementLiteralIntTypeTest() throws IOException {
        String rdfString = "<http://example.org/#roman> <http://example.org/holdsValue> 123 .";

        Collection<Statement> rdf4jStatements = RdfIO.stringToStatements(rdfString, "TURTLE");

        Assertions.assertNotNull(rdf4jStatements);

        Collection<NeoStatement> neoStatements = RdfConverter.rdf4jStatementsToNeoStatements(rdf4jStatements);

        for (NeoStatement s : neoStatements) {
            NeoLiteral literal = (NeoLiteral) s.getObject();

            Assertions.assertTrue(literal.getValue() instanceof Integer);
            Assertions.assertEquals(123, literal.getValue());
        }
    }

    @Test
    public void rdf4jStatementToNeoStatementLiteralDoubleTypeTest() throws IOException {
        String rdfString = "<http://example.org/#roman> <http://example.org/holdsValue> 12.3 .";

        Collection<Statement> rdf4jStatements = RdfIO.stringToStatements(rdfString, "TURTLE");

        Assertions.assertNotNull(rdf4jStatements);

        Collection<NeoStatement> neoStatements = RdfConverter.rdf4jStatementsToNeoStatements(rdf4jStatements);

        for (NeoStatement s : neoStatements) {
            NeoLiteral literal = (NeoLiteral) s.getObject();

            Assertions.assertTrue(literal.getValue() instanceof Double);
            Assertions.assertEquals(12.3, literal.getValue());
        }
    }
}
