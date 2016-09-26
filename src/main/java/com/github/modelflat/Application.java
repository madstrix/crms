package com.github.modelflat;

import com.github.modelflat.util.CommandLineManager;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    private static final Logger log = LogManager.getLogger();

    /**
     * This should remain public and static in order to provide application with startup args.
     */
    public static CommandLineManager commandLineManager;

    public static void main(String[] args) {

        try {
            commandLineManager = new CommandLineManager(args);
        } catch (ParseException e) {
            System.out.println("Failed to start application: bad command line args. Printing stack trace and exiting...");
            e.printStackTrace();
            return;
        }

        if (commandLineManager.shouldPrintHelp()) {
            // print help message and exit
            CommandLineManager.printHelp();
            return;
        }

        if (commandLineManager.checkForOption(CommandLineManager.OPTION_NO_SPRING)) {
            log.info("Application started with no spring context");
        } else {
            startSpring(args);
        }
    }

    /**
     * Start main Spring application. Should be performed
     * on MPI host node only!
     */
    private static void startSpring(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
