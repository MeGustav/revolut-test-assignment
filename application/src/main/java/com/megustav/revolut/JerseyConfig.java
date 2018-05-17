package com.megustav.revolut;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.megustav.revolut.rest.handler.Handler;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.Set;
import java.util.logging.LogManager;

/**
 * Jersey config
 *
 * @author MeGustav
 * 26/04/2018 23:05
 */
@Singleton
public class JerseyConfig extends ResourceConfig {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(JerseyConfig.class);

    @Inject
    public JerseyConfig(Set<Handler> handlers) {
        configureLogging();
        handlers.stream()
                .peek(handler -> log.info("Registering handler '{}'", handler.getHandlerName()))
                .forEach(this::register);
    }

    /**
     * Configuring Jersey logging (it is THAT inconvenient)
     */
    private void configureLogging() {
        // Logging and Jersey is just a nightmare
        register(LoggingFilter.class);
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

}
