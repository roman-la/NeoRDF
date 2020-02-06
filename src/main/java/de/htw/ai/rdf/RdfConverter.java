package de.htw.ai.rdf;

import de.htw.ai.App;
import de.htw.ai.models.NeoElement;
import de.htw.ai.models.NeoIRI;
import de.htw.ai.models.NeoLiteral;
import de.htw.ai.models.NeoStatement;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.util.Collection;
import java.util.LinkedList;

public class RdfConverter {

    public static NeoStatement rdf4jStatementToNeoStatement(Statement rdf4jStatement) {
        NeoElement subject = iriToNeoElement(rdf4jStatement.getSubject().stringValue());
        NeoIRI predicate = (NeoIRI) iriToNeoElement(rdf4jStatement.getPredicate().stringValue());
        NeoElement object = iriToNeoElement(rdf4jStatement.getObject().stringValue());

        return new NeoStatement(subject, predicate, object);
    }

    public static Collection<NeoStatement> rdf4jStatementsToNeoStatements(Collection<Statement> rdf4jStatements) {
        Collection<NeoStatement> neoStatements = new LinkedList<>();

        for (Statement statement : rdf4jStatements)
            neoStatements.add(rdf4jStatementToNeoStatement(statement));

        return neoStatements;
    }

    public static Statement neoStatementToRdf4jStatement(NeoStatement neoStatement) {
        ValueFactory factory = SimpleValueFactory.getInstance();

        Resource subject = (Resource) neoElementToRdf4jValue(neoStatement.getSubject());
        IRI predicate = (IRI) neoElementToRdf4jValue(neoStatement.getPredicate());
        Value object = neoElementToRdf4jValue(neoStatement.getObject());

        return factory.createStatement(subject, predicate, object);
    }

    public static Collection<Statement> neoStatementsToRdf4jStatements(Collection<NeoStatement> neoStatements) {
        Collection<Statement> rdf4jStatements = new LinkedList<>();

        for (NeoStatement neoStatement : neoStatements)
            rdf4jStatements.add(neoStatementToRdf4jStatement(neoStatement));

        return rdf4jStatements;
    }

    private static Value neoElementToRdf4jValue(NeoElement neoElement) {
        ValueFactory factory = SimpleValueFactory.getInstance();

        if (neoElement instanceof NeoIRI) {
            return factory.createIRI((String) ((NeoIRI) neoElement).getProperties().get("iri"));
        } else if (neoElement instanceof NeoLiteral) {
            return factory.createLiteral((String) ((NeoLiteral) neoElement).getValue());
        }

        return null;
    }

    private static NeoElement iriToNeoElement(String iri) {
        if (iri.contains("/")) {
            String namespace = iri.substring(0, iri.lastIndexOf("/") + 1);
            String ns = resolveNamespace(namespace);
            return new NeoIRI(iri, ns, namespace);
        }

        if (iri.matches("-?\\d+"))
            return new NeoLiteral(Integer.parseInt(iri));
        else if (iri.matches("-?\\d+\\.\\d+"))
            return new NeoLiteral(Double.parseDouble(iri));
        else
            return new NeoLiteral(iri);
    }

    private static String resolveNamespace(String namespace) {
        String namespaceAbbreviation = App.ontologyHandler.getOntologyKey(namespace);

        if (namespaceAbbreviation != null)
            return namespaceAbbreviation;

        return App.ontologyHandler.addOntology(namespace);
    }
}
