package de.htw.ai;

import org.apache.commons.cli.*;

public class CliConfiguration {

    private Options options;

    public CliConfiguration() {
        options = new Options();

        options.addOption(Option.builder("name")
            .required(false)
            .hasArg(false)
            .numberOfArgs(1)
            .optionalArg(false)
            .desc("")
            .build());
    }

    public CommandLine parseArgs(String[] args) throws ParseException {
        return new DefaultParser().parse(options, args);
    }

    public void printHelp() {
        new HelpFormatter().printHelp("NeoRDF", options);
    }
}
