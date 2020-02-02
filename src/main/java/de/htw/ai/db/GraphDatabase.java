package de.htw.ai.db;

import de.htw.ai.App;
import de.htw.ai.models.NeoElement;
import de.htw.ai.models.NeoIRI;
import de.htw.ai.models.NeoLiteral;
import de.htw.ai.models.NeoStatement;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

/**
 * @see "github.com/neo4j/neo4j-documentation/blob/4.0/embedded-examples/src/main/java/org/neo4j/examples/EmbeddedNeo4j.java"
 */
public class GraphDatabase {

    private DatabaseManagementService managementService;
    private GraphDatabaseService databaseService;

    public GraphDatabase() {
        String directory = App.config.getConfigValue("dbdirectory");
        managementService = new DatabaseManagementServiceBuilder(new File(directory)).build();
        databaseService = managementService.database(DEFAULT_DATABASE_NAME);

        registerShutdownHook();
    }

    public void insertNeoStatement(NeoStatement statement) {
        try (Transaction tx = databaseService.beginTx()) {
            Node subjectNode = mergeNeoElementAsNode(statement.getSubject(), tx);
            Node objectNode = mergeNeoElementAsNode(statement.getObject(), tx);

            mergeNeoIriRelationship(subjectNode, (NeoIRI) statement.getPredicate(), objectNode);

            tx.commit();
        }
    }

    public Collection<NeoStatement> extractNeoStatements(String query) {
        Collection<NeoStatement> neoStatements = new LinkedList<>();

        Result r = executeQuery(query);

        while (r.hasNext()) {
            NeoElement subject = null;
            NeoElement predicate = null;
            NeoElement object = null;

            for (Map.Entry<String, Object> row : r.next().entrySet()) {
                switch (row.getKey()) {
                    case "s":
                        subject = entityToNeoElement((Entity) row.getValue());
                        break;
                    case "p":
                        predicate = entityToNeoElement((Entity) row.getValue());
                        break;
                    case "o":
                        object = entityToNeoElement((Entity) row.getValue());
                        break;
                }
            }

            neoStatements.add(new NeoStatement(subject, predicate, object));
        }

        return neoStatements;
    }

    private NeoElement entityToNeoElement(Entity entity) {
        NeoElement element;

        Map<String, Object> nodeProperties = entity.getAllProperties();

        if (nodeProperties.containsKey("iri")) {
            element = new NeoIRI((String) nodeProperties.get("iri"), (String) nodeProperties.get("ns"), (String) nodeProperties.get("namespace"));
        } else if (nodeProperties.containsKey("value")) {
            element = new NeoLiteral(nodeProperties.get("value"));
        } else
            return null;

        return element;
    }

    private Node mergeNeoElementAsNode(NeoElement element, Transaction tx) {
        String query;
        Map<String, Object> properties;

        if (element instanceof NeoIRI) {
            query = "MERGE (n:iri {iri: $iri, ns: $ns, namespace: $namespace}) RETURN n";
            properties = ((NeoIRI) element).getProperties();
        } else if (element instanceof NeoLiteral) {
            query = "MERGE (n:literal {value: $value}) RETURN n";
            properties = new HashMap<String, Object>() {{
                put("value", ((NeoLiteral) element).getValue());
            }};
        } else
            return null;

        ResourceIterator<Node> resourceIterator = tx.execute(query, properties).columnAs("n");


        return resourceIterator.next();
    }

    private void mergeNeoIriRelationship(Node subjectNode, NeoIRI neoRelationship, Node objectNode) {
        Relationship relationship = subjectNode.createRelationshipTo(objectNode, RelationshipType.withName("predicate"));

        for (Map.Entry<String, Object> entry : neoRelationship.getProperties().entrySet())
            relationship.setProperty(entry.getKey(), entry.getValue());
    }

    public ResourceIterator<Node> findNodes(Label label, Map<String, Object> nodeProperties) {
        return databaseService.beginTx().findNodes(label, nodeProperties);
    }

    public Result executeQuery(String query) {
        return databaseService.beginTx().execute(query);
    }

    public void shutdown() {
        managementService.shutdown();
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(managementService::shutdown));
    }
}