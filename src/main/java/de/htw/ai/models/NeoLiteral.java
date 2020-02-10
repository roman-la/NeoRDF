package de.htw.ai.models;

/**
 * Representation of a RDF Literal
 */
public class NeoLiteral implements NeoElement {
    private Object value;

    public NeoLiteral(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
