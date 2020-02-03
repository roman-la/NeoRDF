package de.htw.ai.rdf;

import de.htw.ai.App;
import de.htw.ai.models.*;
import de.htw.ai.util.Configuration;
import org.eclipse.rdf4j.model.Statement;
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
        App.config = new Configuration();
        App.config.setConfigValue("ontologies", new File("src/test/resources/ontologiesexample.txt").getAbsolutePath());

        App.ontologyHandler = new OntologyHandler();
    }

    @Test
    public void rdf4jStatementToNeoStatementTest() throws IOException {
        String rdfString = new String(
                Files.readAllBytes(Paths.get(new File("src/test/resources/rdfexample.txt").getAbsolutePath())),
                StandardCharsets.UTF_8);

        Collection<Statement> rdf4jStatements = RdfIO.stringToStatements(rdfString, "TURTLE");

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
}
