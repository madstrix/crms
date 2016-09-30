package com.github.modelflat.util;

import org.apache.commons.cli.*;

import java.lang.reflect.Field;

public class CommandLineManager {

    public static final String APP_NAME = "crms<version>.jar";

    public static final Option OPTION_NO_SPRING =
            Option.builder("S")
                    .longOpt("no-spring")
                    .desc("instruct CRMS not to start spring context")
                    .build();

    public static final Option OPTION_HELP =
            Option.builder("?")
                    .longOpt("help")
                    .desc("display this message")
                    .build();

    public static final Option OPTION_HOST_RANK =
            Option.builder("h")
                    .longOpt("host")
                    .hasArg(true)
                    .optionalArg(false)
                    .type(Integer.class)
                    .argName("host rank")
                    .desc("set host node rank. 0 if omitted")
                    .build();

    private static final String HELP_HEADER = "";
    private static final String HELP_FOOTER = "Source code available at github.com/modelflat/crms";

    private static Options options;
    private CommandLine parsedCommandLine;

    public CommandLineManager(String[] args) throws ParseException {
        parsedCommandLine = new DefaultParser().parse(getOptions(), args);
    }

    public CommandLine getParsedCommandLine() {
        return parsedCommandLine;
    }

    public boolean checkForOption(Option option) {
        return parsedCommandLine.hasOption(option.getLongOpt());
    }

    public boolean shouldPrintHelp() {
        return checkForOption(OPTION_HELP) || parsedCommandLine.getOptions().length == 0;
    }

    public int getHostNodeRank() {
        if (!checkForOption(OPTION_HOST_RANK)) {
            return 0;
        }
        return Integer.valueOf(parsedCommandLine.getOptionValue(OPTION_HOST_RANK.getLongOpt()));
    }

    public static Options getOptions() {
        if (options != null) {
            return options;
        }
        options = new Options();
        for (Field f : CommandLineManager.class.getFields()) {
            if (f.getName().startsWith("OPTION_") && f.getType() == Option.class) {
                try {
                    options.addOption((Option) f.get(null));
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        return options;
    }

    public static void printHelp() {
        new HelpFormatter().printHelp(APP_NAME, HELP_HEADER, getOptions(), HELP_FOOTER, true);
    }
}
