package com.megustav.revolut;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application entry point
 *
 * @author MeGustav
 * 25/04/2018 22:29
 */
public class Main {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    /** Jetty port */
    private static final int PORT = 13034;

    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT);
        log.info("Starting server...");
        server.start();
        server.join();
    }

}
