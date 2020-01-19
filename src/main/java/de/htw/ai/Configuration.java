package de.htw.ai;

import org.apache.commons.cli.*;

public class Configuration {

    private Options options;

    public Configuration() {
        options = new Options();

        options.addOption(Option.builder("name")
                .required(false)
                .hasArg(false)
                .numberOfArgs(1)
                .optionalArg(false)
                .desc("")
                .build());
    }

    public boolean parse(String[] args) {
        try {
            new DefaultParser().parse(options, args);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public void printHelp() {
        new HelpFormatter().printHelp("NeoRDF", options);
    }
}
