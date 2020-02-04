package de.htw.ai.util;

import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    public boolean defaultConfig;
    private Options options;
    private Properties properties;

    public Configuration() {
        properties = new Properties();

        properties.put("ontologies", "/de/htw/ai/ontologies.txt");
        properties.put("dbdirectory", "/db");
        properties.put("port", "8080");

        defaultConfig = true;

        options = new Options();

        options.addOption(Option.builder("config")
                .required(false)
                .hasArg(true)
                .numberOfArgs(1)
                .optionalArg(false)
                .desc("Path to config file")
                .build());
    }

    public void parse(String[] args) throws ParseException, IOException {
        CommandLine commandLine = new DefaultParser().parse(options, args);

        if (commandLine.hasOption("config"))
            try (InputStream in = new FileInputStream(commandLine.getOptionValue("config"))) {
                properties.load(in);

                defaultConfig = false;
            }
    }

    public String getConfigValue(String key) {
        return properties.getProperty(key);
    }

    public void setConfigValue(String key, String value) {
        properties.put(key, value);
    }

    public void printHelp() {
        new HelpFormatter().printHelp("NeoRDF", options);
    }
}
