package de.htw.ai;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import de.htw.ai.db.GraphDatabase;
import de.htw.ai.rdf.OntologyHandler;
import de.htw.ai.rest.HttpServer;
import de.htw.ai.util.Configuration;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class App {

    public static Configuration config;
    public static GraphDatabase database;
    public static OntologyHandler ontologyHandler;
    public static HttpServer httpServer;
    private static Logger logger = LoggerFactory.getLogger(App.class);
    private static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    public static void main(String[] args) {
        logger.info("Starting NeoRDF");

        config = new Configuration();

        try {
            config.parse(args);
        } catch (IOException e) {
            logger.error("An error occurred while parsing configuration", e);
            return;
        } catch (ParseException e) {
            config.printHelp();
        }

        switch (config.getConfigValue("loglevel")) {
            case "debug":
                loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.DEBUG);
                break;
            case "info":
                loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO);
                break;
            case "warn":
                loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.WARN);
                break;
            case "error":
                loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.ERROR);
                break;
        }

        ontologyHandler = new OntologyHandler();

        try {
            database = new GraphDatabase();
        } catch (Exception e) {
            logger.error("An error occurred in graph database", e);
        }

        httpServer = new HttpServer();
        try {
            httpServer.start();
        } catch (Exception e) {
            logger.error("An error occurred in web server", e);
            return;
        }

        registerShutdownHook();
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down NeoRDF");
            database.shutdown();
            httpServer.stop();

            loggerContext.stop();
        }));
    }
}
