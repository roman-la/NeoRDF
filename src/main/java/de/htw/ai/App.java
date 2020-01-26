package de.htw.ai;

import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;

public class App {

    public static Configuration config;
    public static EmbeddedNeo4jDatabase database;

    public static void main(String[] args) {
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

        database = new EmbeddedNeo4jDatabase(new File("target/dbtest"));

        new RestApi().start();
    }
}
