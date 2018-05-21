package com.megustav.revolut;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.megustav.revolut.database.LiquibaseRunner;
import com.megustav.revolut.module.ConfigurationModule;
import com.megustav.revolut.module.HandlersModule;
import com.megustav.revolut.module.MiscModule;
import com.megustav.revolut.module.PersistenceModule;
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
        Injector injector = Guice.createInjector(
                new ConfigurationModule(),
                new PersistenceModule(),
                new HandlersModule(),
                new MiscModule()
        );
        log.info("Preparing DB...");
        injector.getInstance(LiquibaseRunner.class).applyChangeLog();
        log.info("DB prepared");

        log.info("Starting server...");
        Server jetty = injector.getInstance(JettyServer.class).getServer();
        jetty.start();
        jetty.join();
    }

}
