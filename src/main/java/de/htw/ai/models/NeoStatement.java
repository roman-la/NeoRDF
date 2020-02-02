package de.htw.ai.models;

public class NeoStatement {
    private NeoElement subject;
    private NeoIRI predicate;
    private NeoElement object;

    public NeoStatement(NeoElement subject, NeoIRI predicate, NeoElement object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public NeoElement getSubject() {
        return subject;
    }

    public NeoIRI getPredicate() {
        return predicate;
    }

    public NeoElement getObject() {
        return object;
    }
}
