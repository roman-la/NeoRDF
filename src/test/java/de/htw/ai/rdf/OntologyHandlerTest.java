package de.htw.ai.rdf;

import de.htw.ai.App;
import de.htw.ai.config.NeoConfiguration;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OntologyHandlerTest {

    private static String ontologiesPath = new File("src/test/resources/ontologiesexample.txt").getAbsolutePath();

    @BeforeAll
    public static void beforeAll() {
        App.config = new NeoConfiguration();
        App.config.setConfigValue("ontologies", ontologiesPath);
    }

    @AfterEach
    public void afterEach() throws IOException {
        String defaultContent = "owl http://www.w3.org/2002/07/owl#\n" +
                "rdf http://www.w3.org/1999/02/22-rdf-syntax-ns#\n" +
                "rdfs http://www.w3.org/2000/01/rdf-schema#\n" +
                "foaf http://xmlns.com/foaf/0.1/";

        Files.write(Paths.get(ontologiesPath), defaultContent.getBytes());
    }

    @Test
    public void getOntologyKeyTest() {
        OntologyHandler ontologyHandler = new OntologyHandler();

        Assertions.assertEquals("owl", ontologyHandler.getOntologyKey("http://www.w3.org/2002/07/owl#"));
        Assertions.assertEquals("rdf", ontologyHandler.getOntologyKey("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
        Assertions.assertEquals("rdfs", ontologyHandler.getOntologyKey("http://www.w3.org/2000/01/rdf-schema#"));

        Assertions.assertNull(ontologyHandler.getOntologyKey("http://www.example.org/"));
    }

    @Test
    public void addOntologyTest() {
        OntologyHandler ontologyHandler = new OntologyHandler();

        Assertions.assertNull(ontologyHandler.getOntologyKey("http://www.example.org/"));

        ontologyHandler.addOntology("ex", "http://www.example.org/");

        Assertions.assertEquals("ex", ontologyHandler.getOntologyKey("http://www.example.org/"));

        String ontologyAbbreviation = ontologyHandler.addOntology("http://www.htw-berlin.de/");

        Assertions.assertEquals(4, ontologyAbbreviation.length());

        Assertions.assertEquals(ontologyAbbreviation, ontologyHandler.getOntologyKey("http://www.htw-berlin.de/"));
    }

    @Test
    public void persistOntologyTest() throws IOException {
        OntologyHandler ontologyHandler = new OntologyHandler();

        Assertions.assertNull(ontologyHandler.getOntologyKey("http://www.example.org/"));

        ontologyHandler.addOntology("ex", "http://www.example.org/");

        Assertions.assertTrue(Files.lines(Paths.get(ontologiesPath)).anyMatch(x -> x.equals("ex http://www.example.org/")));
    }
}