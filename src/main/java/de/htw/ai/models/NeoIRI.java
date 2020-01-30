package de.htw.ai.models;

import java.util.HashMap;
import java.util.Map;

public class NeoIRI implements NeoElement {
    private String iri;
    private String ns;
    private String namespace;

    public NeoIRI(String iri, String ns, String namespace) {
        this.iri = iri;
        this.ns = ns;
        this.namespace = namespace;
    }

    public Map<String, Object> getProperties() {
        return new HashMap<String, Object>() {{
            put("iri", iri);
            put("ns", ns);
            put("namespace", namespace);
        }};
    }
}
