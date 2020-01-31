package de.htw.ai.rdf;

import de.htw.ai.models.NeoElement;
import de.htw.ai.models.NeoIRI;
import de.htw.ai.models.NeoLiteral;
import de.htw.ai.models.NeoStatement;
import org.eclipse.rdf4j.model.Statement;

import java.util.Collection;
import java.util.LinkedList;

public class RdfConverter {

    public static NeoStatement rdf4jStatementToNeoStatement(Statement rdf4jStatement) {
        NeoElement subject = iriToNeoElement(rdf4jStatement.getSubject().stringValue());
        NeoElement predicate = iriToNeoElement(rdf4jStatement.getPredicate().stringValue());
        NeoElement object = iriToNeoElement(rdf4jStatement.getObject().stringValue());

        return new NeoStatement(subject, predicate, object);
    }

    public static Collection<NeoStatement> rdf4jStatementsToNeoStatements(Collection<Statement> rdf4jStatements) {
        Collection<NeoStatement> neoStatements = new LinkedList<>();

        for (Statement statement : rdf4jStatements)
            neoStatements.add(rdf4jStatementToNeoStatement(statement));

        return neoStatements;
    }

    private static NeoElement iriToNeoElement(String iri) {
        if (iri.contains("/")) {
            String namespace = iri.substring(0, iri.lastIndexOf("/") + 1);
            String ns = resolveNamespace(namespace);
            return new NeoIRI(iri, ns, namespace);
        }

        return new NeoLiteral(iri);
    }

    private static String resolveNamespace(String namespace) {
        String namespaceAbbreviation = OntologyHandler.getInstance().getOntologyKey(namespace);

        if (namespaceAbbreviation != null)
            return namespaceAbbreviation;

        return OntologyHandler.getInstance().addOntology(namespace);
    }
}
