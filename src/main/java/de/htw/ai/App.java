package de.htw.ai;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import de.htw.ai.db.GraphDatabase;
import de.htw.ai.rdf.OntologyHandler;
import de.htw.ai.rest.HttpServer;
import de.htw.ai.config.NeoConfiguration;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class App {

    public static NeoConfiguration config;
    public static GraphDatabase database;
    public static OntologyHandler ontologyHandler;
    public static HttpServer httpServer;
    private static Logger logger = LoggerFactory.getLogger(App.class);
    private static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    /**
     * Main method of the NeoRDF program.
     * It hands over the args for configuration and starts all services.
     *
     * @param args May contain the path to a config file
     */
    public static void main(String[] args) {
        logger.info("Starting NeoRDF");

        // Setup configuration
        config = new NeoConfiguration();
        try {
            config.parse(args);
        } catch (IOException e) {
            logger.error("An error occurred while reading configuration file", e);
            return;
        } catch (ParseException e) {
            logger.error("An error occurred while parsing command line arguments", e);
            config.printHelp();
            return;
        }

        // Set logger level
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
            default:
                logger.error("Unknown log level");
        }

        ontologyHandler = new OntologyHandler();

        // Start graph database
        database = new GraphDatabase();
        try {
            database.start();
        } catch (Exception e) {
            logger.error("An error occurred in graph database", e);
            return;
        }

        // Start http server
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
