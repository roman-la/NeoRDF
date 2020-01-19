package de.htw.ai;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;

import java.io.File;
import java.util.Map;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

/**
 * @see "github.com/neo4j/neo4j-documentation/blob/4.0/embedded-examples/src/main/java/org/neo4j/examples/EmbeddedNeo4j.java"
 */
public class EmbeddedNeo4jDatabase {

    private DatabaseManagementService managementService;
    private GraphDatabaseService databaseService;

    public EmbeddedNeo4jDatabase(File directory) {
        // TODO: Handle database values like cache, etc
        managementService = new DatabaseManagementServiceBuilder(directory).build();
        databaseService = managementService.database(DEFAULT_DATABASE_NAME);
        registerShutdownHook(managementService);
    }

    public Node createNode(Map<String, Object> nodeProperties) {
        if (!validateProperties(nodeProperties))
            return null;

        Transaction tx = databaseService.beginTx();

        // Create a node and set its label and properties
        Node node = tx.createNode(Label.label((String) nodeProperties.get("type")));
        for (Map.Entry<String, Object> entry : nodeProperties.entrySet()) {
            if (!entry.getKey().equals("type"))
                node.setProperty(entry.getKey(), entry.getValue());
        }

        tx.commit();

        return node;
    }

    public Relationship setRelationship(Node firstNode, Node secondNode, Map<String, Object> relationshipProperties) {
        if (!validateProperties(relationshipProperties))
            return null;

        Transaction tx = databaseService.beginTx();

        // Create a relationship between first and second node and set its type and properties
        Relationship relationship = firstNode.createRelationshipTo(secondNode, (RelationshipType) relationshipProperties.get("type"));
        for (Map.Entry<String, Object> entry : relationshipProperties.entrySet()) {
            if (!entry.getKey().equals("type"))
                relationship.setProperty(entry.getKey(), entry.getValue());
        }

        tx.commit();

        return relationship;
    }

    public ResourceIterator<Node> findNodes(Label label, Map<String, Object> nodeProperties) {
        return databaseService.beginTx().findNodes(label, nodeProperties);
    }

    public Result executeQuery(String query) {
        return databaseService.beginTx().execute(query);
    }

    private boolean validateProperties(Map<String, Object> properties) {
        if (!(properties.get("type") instanceof String))
            return false;

        // Maybe check if all values are valid types (see neo4j.com/docs/java-reference/current/java-embedded/property-values/)

        return true;
    }

    private void registerShutdownHook(final DatabaseManagementService managementService) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                managementService.shutdown();
            }
        });
    }
}
