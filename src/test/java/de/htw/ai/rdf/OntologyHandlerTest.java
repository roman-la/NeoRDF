package de.htw.ai.rdf;

import de.htw.ai.App;
import de.htw.ai.util.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OntologyHandlerTest {

    @BeforeAll
    public void beforeAll() {
        App.config = new Configuration();
        App.config.setConfigValue("ontologies", getClass().getClassLoader().getResource("ontologiesexample.txt").getFile());
    }

    @Test
    public void getOntologyKeyTest() {
        Assertions.assertEquals("owl", OntologyHandler.getInstance().getOntologyKey("http://www.w3.org/2002/07/owl#"));
        Assertions.assertEquals("rdf", OntologyHandler.getInstance().getOntologyKey("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
        Assertions.assertEquals("rdfs", OntologyHandler.getInstance().getOntologyKey("http://www.w3.org/2000/01/rdf-schema#"));

        Assertions.assertNull(OntologyHandler.getInstance().getOntologyKey("http://www.example.org/"));
    }

    @Test
    public void addOntologyTest() {
        OntologyHandler.getInstance().addOntology("ex", "http://www.example.org/");

        Assertions.assertEquals("ex", OntologyHandler.getInstance().getOntologyKey("http://www.example.org/"));

        String ontologyAbbreviation = OntologyHandler.getInstance().addOntology("http://www.htw-berlin.de/");

        Assertions.assertEquals(4, ontologyAbbreviation.length());

        Assertions.assertEquals(ontologyAbbreviation, OntologyHandler.getInstance().getOntologyKey("http://www.htw-berlin.de/"));
    }
}