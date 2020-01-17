package de.htw.ai;

import org.apache.commons.cli.ParseException;

public class App
{
    public static void main( String[] args )
    {
        CliConfiguration configuration = new CliConfiguration();

        try {
            configuration.parseArgs(args);
        } catch (ParseException e) {
            configuration.printHelp();
        }
    }
}
