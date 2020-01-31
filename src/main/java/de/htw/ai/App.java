package de.htw.ai;

import de.htw.ai.db.GraphDatabase;
import de.htw.ai.rest.RestApi;
import de.htw.ai.util.Configuration;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;

public class App {

    public static Configuration config;
    public static GraphDatabase database;

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

        //database = new GraphDatabase(new File("target/dbtest"));

        new RestApi().start();
    }
}
