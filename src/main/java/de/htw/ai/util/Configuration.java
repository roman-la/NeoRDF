package de.htw.ai.util;

import org.apache.commons.cli.*;
import scala.util.control.Exception;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Configuration {

    private Options options;
    private Properties properties;

    public Configuration() {
        properties = new Properties();

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

        String configPath;

        if (commandLine.hasOption("config"))
            configPath = commandLine.getOptionValue("config");
        else
            configPath = "/config.txt";

        loadProperties(configPath);
    }

    private void loadProperties(String configPath) throws IOException {
        try (InputStream in = new FileInputStream(configPath)) {
            properties.load(in);
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
