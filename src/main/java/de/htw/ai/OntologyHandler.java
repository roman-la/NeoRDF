package de.htw.ai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OntologyHandler {

    private static OntologyHandler instance;
    private Map<String, String> iris;

    private OntologyHandler() {
        iris = new HashMap<>();

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
        for (Map.Entry<String, String> entry : iris.entrySet()) {
            if (entry.getValue().equals(iri))
                return entry.getKey();
        }

        return null;
    }

    public String addOntology(String iri) {
        String randomId;

        do {
            randomId = UUID.randomUUID().toString().substring(0, 3);
        } while (iris.containsKey(randomId));

        iris.put(randomId, iri);

        return randomId;
    }

    private void loadOntologies(String path) throws IOException {
        Files.lines(Paths.get(path)).forEach(x -> iris.put(x.split(" ")[0], x.split(" ")[1]));
    }
}
