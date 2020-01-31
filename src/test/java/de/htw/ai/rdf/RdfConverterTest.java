package de.htw.ai.rdf;

import de.htw.ai.App;
import de.htw.ai.models.*;
import de.htw.ai.util.Configuration;
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
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RdfConverterTest {

    @BeforeAll
    public void beforeAll() {
        App.config = new Configuration();
        App.config.setConfigValue("ontologies", getClass().getClassLoader().getResource("ontologiesexample.txt").getFile());
    }

    @Test
    public void rdf4jStatementToNeoStatementTest() throws IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource("rdfexample.txt").getPath());
        String rdfString = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        Collection<Statement> rdf4jStatements = RdfIO.stringToStatements(rdfString, RDFFormat.TURTLE);

        Collection<NeoStatement> neoStatements = RdfConverter.rdf4jStatementsToNeoStatements(rdf4jStatements);

        Assertions.assertEquals(9, neoStatements.size());

        for (NeoStatement s : neoStatements) {
            NeoIRI sub = (NeoIRI) s.getSubject();
            NeoIRI pre = (NeoIRI) s.getPredicate();

            System.out.println("Subject");
            for (Map.Entry<String, Object> entry : sub.getProperties().entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }

            System.out.println("Predicate");
            for (Map.Entry<String, Object> entry : pre.getProperties().entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }

            System.out.println("Object");
            if (s.getObject() instanceof NeoIRI) {
                NeoIRI obj = (NeoIRI) s.getObject();

                for (Map.Entry<String, Object> entry : obj.getProperties().entrySet()) {
                    System.out.println(entry.getKey() + " " + entry.getValue());
                }
            } else {
                NeoLiteral obj = (NeoLiteral) s.getObject();

                System.out.println("value: " + obj.getValue());
            }

            System.out.println("-----------------");
        }
    }
}
