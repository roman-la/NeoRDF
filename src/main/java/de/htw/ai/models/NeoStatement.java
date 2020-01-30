package de.htw.ai.models;

public class NeoStatement {
    private NeoElement subject;
    private NeoElement predicate;
    private NeoElement object;

    public NeoStatement(NeoElement subject, NeoElement predicate, NeoElement object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public NeoElement getSubject() {
        return subject;
    }

    public NeoElement getPredicate() {
        return predicate;
    }

    public NeoElement getObject() {
        return object;
    }
}
