package com.megustav.revolut;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.megustav.revolut.handler.Handler;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

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
        handlers.stream()
                .peek(handler -> log.info("Registering handler '{}'", handler.getHandlerName()))
                .forEach(this::register);
    }
}
