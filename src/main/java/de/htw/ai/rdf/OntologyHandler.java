package de.htw.ai.rdf;

import de.htw.ai.App;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OntologyHandler {

    private Map<String, String> ontologies;

    public OntologyHandler() {
        ontologies = new HashMap<>();

        try {
            loadOntologies();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void loadOntologies() throws IOException {
        InputStream inputStream = new FileInputStream(App.config.getConfigValue("ontologies"));

        String ontologiesFile = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        for (String line : ontologiesFile.split("(\\r?\\n)+"))
            ontologies.put(line.split(" ")[0], line.split(" ")[1]);
    }

    private void persistOntology(String abbreviation, String namespace) {
        String toPersist = System.lineSeparator() + abbreviation + " " + namespace;

        try {
            Files.write(Paths.get(App.config.getConfigValue("ontologies")), toPersist.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String ontologiesToString() {
        return ontologies.entrySet().stream().map(e -> e.getKey() + " " + e.getValue()).collect(Collectors.joining(System.lineSeparator()));
    }
}
