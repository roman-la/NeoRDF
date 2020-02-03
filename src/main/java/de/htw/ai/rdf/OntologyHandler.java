package de.htw.ai.rdf;

import de.htw.ai.App;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class OntologyHandler {

    private Map<String, String> ontologies;
    private String ontologyFile = App.config.getConfigValue("ontologies");

    public OntologyHandler() {
        ontologies = new HashMap<>();

        loadOntologies(ontologyFile);
    }

    public String getOntologyKey(String iri) {
        for (Map.Entry<String, String> entry : ontologies.entrySet())
            if (entry.getValue().equals(iri))
                return entry.getKey();

        return null;
    }

    public String addOntology(String abbreviation, String namespace) {
        if (!ontologies.containsKey(abbreviation)) {
            ontologies.put(abbreviation, namespace);

            persistOntology(abbreviation, namespace);

            return abbreviation;
        } else if (ontologies.get(abbreviation).equals(namespace))
            return abbreviation;
        else
            return addOntology(namespace);
    }

    public String addOntology(String namespace) {
        String randomAbbreviation;

        do {
            randomAbbreviation = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        } while (ontologies.containsKey(randomAbbreviation));

        ontologies.put(randomAbbreviation, namespace);

        persistOntology(randomAbbreviation, namespace);

        return randomAbbreviation;
    }

    private void loadOntologies(String path) {
        try {
            Files.lines(Paths.get(path)).forEach(x -> ontologies.put(x.split(" ")[0], x.split(" ")[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void persistOntology(String abbreviation, String namespace) {
        String toPersist = System.lineSeparator() + abbreviation + " " + namespace;

        try {
            Files.write(Paths.get(ontologyFile), toPersist.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }
}
