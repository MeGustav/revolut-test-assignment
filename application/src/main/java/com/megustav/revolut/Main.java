package com.megustav.revolut;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.megustav.revolut.module.ConfigurationModule;
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

    public static void main(String[] args) throws Exception {
        log.info("Starting server...");
        Injector base = Guice.createInjector(new ConfigurationModule());
        Server jetty = base.getInstance(JettyServer.class).getServer();
        jetty.start();
        jetty.join();
    }

}
