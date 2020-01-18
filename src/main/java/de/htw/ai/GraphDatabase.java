package de.htw.ai;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;

import java.io.File;
import java.util.Map;

public class GraphDatabase {

    private DatabaseManagementService managementService;
    private GraphDatabaseService databaseService;


    public GraphDatabase(File directory, String pathToConfig, String databaseName) {
        // TODO: Handle more database values like cache, etc
        managementService = new DatabaseManagementServiceBuilder(directory).loadPropertiesFromFile(pathToConfig).build();
        databaseService = managementService.database(databaseName);
        registerShutdownHook();
    }

    public Node createNode(Map<String, Object> nodeProperties) {
        Transaction tx = databaseService.beginTx();

        // Create node and set its properties
        Node node = tx.createNode();
        for (Map.Entry<String, Object> entry : nodeProperties.entrySet()) {
            node.setProperty(entry.getKey(), entry.getValue());
        }

        tx.commit();

        return node;
    }

    public Relationship setRelationship(Node firstNode, Node secondNode, Map<String, Object> relationshipProperties) {
        Transaction tx = databaseService.beginTx();

        // Create relationship between first and second node and set its properties
        Relationship relationship = firstNode.createRelationshipTo(secondNode, (RelationshipType) relationshipProperties.get("type"));
        for (Map.Entry<String, Object> entry : relationshipProperties.entrySet()) {
            relationship.setProperty(entry.getKey(), entry.getValue());
        }

        tx.commit();

        return relationship;
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                managementService.shutdown();
            }
        });
    }
}
