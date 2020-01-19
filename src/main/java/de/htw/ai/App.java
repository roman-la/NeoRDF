package de.htw.ai;

public class App {
    public static void main(String[] args) {
        Configuration config = new Configuration();

        if (!config.parse(args)) {
            config.printHelp();
            return;
        }

        new RestApi().start();
    }
}
