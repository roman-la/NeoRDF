package de.htw.ai;

import org.eclipse.rdf4j.model.Statement;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class RdfConverter {
    public static Map<String, Object> StatementToNodeMap(Statement statement) {
        Map<String, Object> node = new HashMap<>();

        node.put("subject", resolveNamespace(statement.getSubject().stringValue()));
        node.put("predicate", resolveNamespace(statement.getPredicate().stringValue()));
        node.put("object", resolveNamespace(statement.getObject().stringValue()));

        return node;
    }

    private static String resolveNamespace(String iri) {
        if (iri.contains("/")) {
            String namespace = iri.substring(0, iri.lastIndexOf("/"));
            String namespaceAbbreviation = OntologyHandler.getInstance().getOntologyKey(namespace);

            if (namespaceAbbreviation != null)
                return namespaceAbbreviation + ":" + iri.replace(namespace, "");

            return OntologyHandler.getInstance().addOntology(namespace) + ":" + iri.replace(namespace, "");
        }

        return iri;
    }

    public static Collection<Map<String, Object>> StatementsToNodeMaps(Collection<Statement> statements) {
        Collection<Map<String, Object>> nodes = new LinkedList<>();

        for (Statement statement : statements)
            nodes.add(StatementToNodeMap(statement));

        return nodes;
    }
}
