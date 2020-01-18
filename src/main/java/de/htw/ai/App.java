package de.htw.ai;

import org.apache.commons.cli.*;

public class App {
    public static void main(String[] args) {
        Options options = new Options();

        options.addOption(Option.builder("name")
                .required(false)
                .hasArg(false)
                .numberOfArgs(1)
                .optionalArg(false)
                .desc("")
                .build());

        try {
            new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            new HelpFormatter().printHelp("NeoRDF", options);
        }
    }
}
