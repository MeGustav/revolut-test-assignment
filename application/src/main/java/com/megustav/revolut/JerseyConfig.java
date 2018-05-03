package com.megustav.revolut;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.megustav.revolut.handler.OperationHandler;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Jersey config
 *
 * @author MeGustav
 * 26/04/2018 23:05
 */
@Singleton
public class JerseyConfig extends ResourceConfig {

    @Inject
    public JerseyConfig(OperationHandler handler) {
        register(handler);
    }
}
