package de.htw.ai;

import java.io.File;

public class App {
    public static void main(String[] args) {
        Configuration config = new Configuration();

        if (!config.parse(args)) {
            config.printHelp();
            return;
        }

        new EmbeddedNeo4jDatabase(new File("target/dbtest"));

        new RestApi().start();
    }
}
