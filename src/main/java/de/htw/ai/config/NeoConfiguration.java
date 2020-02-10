package de.htw.ai.config;

import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class NeoConfiguration {

    private Options options;
    private Properties properties;

    public NeoConfiguration() {
        properties = new Properties(getDefaultProperties());

        options = getOptions();
    }

    public void parse(String[] args) throws IOException, ParseException {
        CommandLine commandLine = new DefaultParser().parse(options, args);

        if (commandLine.hasOption("config")) {
            InputStream inputStream = new FileInputStream(commandLine.getOptionValue("config"));

            properties.load(inputStream);

            inputStream.close();
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

    private Properties getDefaultProperties() {
        Properties defaultProperties = new Properties();

        defaultProperties.put("ontologies", System.getProperty("user.dir") + "/ontologies.txt");
        defaultProperties.put("dbdir", System.getProperty("user.dir") + "/db");
        defaultProperties.put("port", "8080");
        defaultProperties.put("loglevel", "info");

        return defaultProperties;
    }

    private Options getOptions() {
        Options options = new Options();

        options.addOption(Option.builder("config")
                .required(false)
                .hasArg(true)
                .numberOfArgs(1)
                .optionalArg(false)
                .desc("Path to config file")
                .build());

        return options;
    }
}
