package de.htw.ai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OntologyHandler {

    private static OntologyHandler instance;
    private Map<String, String> ontologies;

    private OntologyHandler() {
        ontologies = new HashMap<>();

        try {
            loadOntologies(App.config.getConfigValue("ontologies"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized OntologyHandler getInstance() {
        if (instance == null)
            instance = new OntologyHandler();

        return instance;
    }

    public String getOntologyKey(String iri) {
        for (Map.Entry<String, String> entry : ontologies.entrySet()) {
            if (entry.getValue().equals(iri))
                return entry.getKey();
        }

        return null;
    }

    public void addOntology(String abbreviation, String namespace) {
        ontologies.put(abbreviation, namespace);
    }

    public String addOntology(String namespace) {
        String randomAbbreviation;

        do {
            randomAbbreviation = UUID.randomUUID().toString().substring(0, 3);
        } while (ontologies.containsKey(randomAbbreviation));

        ontologies.put(randomAbbreviation, namespace);

        return randomAbbreviation;
    }

    private void loadOntologies(String path) throws IOException {
        Files.lines(Paths.get(path)).forEach(x -> ontologies.put(x.split(" ")[0], x.split(" ")[1]));
    }
}
