package de.htw.ai;

import de.htw.ai.db.GraphDatabase;
import de.htw.ai.rdf.OntologyHandler;
import de.htw.ai.rest.HttpServer;
import de.htw.ai.util.Configuration;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

public class App {

    public static Configuration config;
    public static GraphDatabase database;
    public static OntologyHandler ontologyHandler;
    public static HttpServer httpServer;

    public static void main(String[] args) {
        config = new Configuration();

        try {
            config.parse(args);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ontologyHandler = new OntologyHandler();

        database = new GraphDatabase();

        httpServer = new HttpServer();
        try {
            httpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        registerShutdownHook();
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                database.shutdown();
                httpServer.stop();
            }
        });
    }
}
