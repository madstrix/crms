package com.github.modelflat;

import com.github.modelflat.util.CommandLineManager;
import com.jogamp.common.JogampRuntimeException;
import com.jogamp.opencl.CLPlatform;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import mpi.*;

@SpringBootApplication
public class Application {

    private static final Logger log = LogManager.getLogger();

    private static CommandLineManager commandLineManager;
    private static boolean hasMPI = false;
    private static boolean hasCL = false;
    private static boolean onHost = true;
    private static int rank = 0;

    public static void main(String[] args) {

        // first, try to parse all arguments
        try {
            commandLineManager = new CommandLineManager(args);
        } catch (ParseException e) {
            System.out.println("Failed to start application: bad command line args.");
            System.out.println(e.getMessage());
            return;
        }

        // then detect if help requested
        if (commandLineManager.shouldPrintHelp()) {
            // if so print help message and exit
            CommandLineManager.printHelp();
            return;
        }

        // MPI
        if (hasMPI = initializeMPI(args)) {
            obtainMPIRuntimeInfo();
        }

        // CL
        hasCL = initializeCL();

        // TODO: decide, remove it or not
        if (!onHost || commandLineManager.checkForOption(CommandLineManager.OPTION_NO_SPRING)) {
            log.info("Application started with no spring context.");
        } else {
            // start spring
            startSpring(args);
        }

        /*check mpi*/
        try {
            MPI.Init(args);
            int mpiSize = MPI.COMM_WORLD.getSize();
            MPI.Finalize();
            log.info("Application started with MPI on " + mpiSize + " nodes.");
        } catch (MPIException e) {
            e.printStackTrace();
        } catch (UnsatisfiedLinkError e){
            log.info("Application started with no MPI context");
        }

    }

    /**
     * Method to finalize MPI
     */
    public static void finalizeMPI() {
        try {
            MPI.Finalize();
        } catch (MPIException e) {
            log.error("An error occured during MPI finalization: ", e);
        } finally {
            hasMPI = false;
        }
    }

    /**
     * Tries to init MPI.
     *
     * @return was MPI initialized or not
     */
    private static boolean initializeMPI(String args[]) {
        try {
            MPI.Init(args);
        } catch (MPIException e) {
            log.info("MPI error occured during initialization: " + e.getMessage());
            return false;
        } catch (UnsatisfiedLinkError e){
            log.info("No MPI native lib found.");
            return false;
        }
        // seems ok
        try {
            log.info(String.format(
                    "Application started with MPI on %d nodes. Current node is %d (%s, %s).",
                    MPI.COMM_WORLD.getSize(),
                    MPI.COMM_WORLD.getRank(),
                    MPI.COMM_WORLD.getName(),
                    onHost ? "host node" : "slave node"));
        } catch (MPIException e) {
            // if any of above methods throws exception, we should terminate MPI - something went really wrong
            log.error(e);
            finalizeMPI();
            return false;
        }
        return true;
    }

    /**
     * fill in fields with useful and intensively used info:
     * <table>
     *     <tr>
     *         onHost
     *     </tr>
     *     <tr>
     *         rank
     *     </tr>
     * </table>
     */
    private static void obtainMPIRuntimeInfo() {
        try {
            rank = MPI.COMM_WORLD.getRank();
            onHost = rank == commandLineManager.getHostNodeRank();
        } catch (MPIException e) {
            log.error(e);
        }
    }

    private static boolean initializeCL() {
        try {
            CLPlatform.initialize();
        } catch (JogampRuntimeException e) {
            log.info("CL context cannot be loaded.", e);
            return false;
        }
        log.info("Application started with CL context presented.");
        return true;
    }

    /**
     * Start main Spring application. Should be performed
     * on MPI host node only!
     */
    private static void startSpring(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public static CommandLineManager getCommandLine() {
        return commandLineManager;
    }

    public static boolean isOnHost() {
        return onHost;
    }

    public static int getRank() {
        return rank;
    }

    public static boolean hasMPI() {
        return hasMPI;
    }

    public static boolean hasCL() {
        return hasCL;
    }
}
