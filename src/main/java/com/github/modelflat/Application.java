package com.github.modelflat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        startSpring(args);
    }

    /**
     * Start main Spring application. Should be performed
     * on MPI host node only!
     */
    private static void startSpring(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
