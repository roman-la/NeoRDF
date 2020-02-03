package de.htw.ai;

import de.htw.ai.db.GraphDatabase;
import de.htw.ai.rdf.OntologyHandler;
import de.htw.ai.rest.HttpServer;
import de.htw.ai.util.Configuration;

public class App {

    public static Configuration config;
    public static GraphDatabase database;
    public static OntologyHandler ontologyHandler;
    public static HttpServer restApi;

    public static void main(String[] args) {
        /*
        config = new Configuration();

        try {
            config.parse(args);
        } catch (ParseException e) {
            config.printHelp();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
         */
    }
}
