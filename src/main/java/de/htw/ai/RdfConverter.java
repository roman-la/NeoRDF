package de.htw.ai;

import org.eclipse.rdf4j.model.Statement;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class RdfConverter {
    public static Map<String, Object> StatementToNodeMap(Statement statement) {
        Map<String, Object> node = new HashMap<>();

        node.put("subject", statement.getSubject());
        node.put("predicate", statement.getPredicate());
        node.put("object", statement.getObject());

        return node;
    }

    public static Collection<Map<String, Object>> StatementsToNodeMaps(Collection<Statement> statements) {
        Collection<Map<String, Object>> nodes = new LinkedList<>();

        for (Statement statement : statements)
            nodes.add(StatementToNodeMap(statement));

        return nodes;
    }
}
