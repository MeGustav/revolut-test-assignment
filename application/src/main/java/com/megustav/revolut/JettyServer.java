package com.megustav.revolut;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.megustav.revolut.configuration.ApplicationConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Jetty server wrapper for configuration
 *
 * @author MeGustav
 * 26/04/2018 19:58
 */
@Singleton
public class JettyServer {

    /** Jetty server */
    private final Server server;

    @Inject
    public JettyServer(ApplicationConfiguration configuration, JerseyConfig jersey) {
        this.server = new Server(configuration.getServerPort());
        ServletContextHandler handler = createHandler(jersey);
        server.setHandler(handler);
    }

    /**
     * Create Jersey handler
     *
     * @param jersey jersey configuration
     * @return {@link ServletContextHandler} instance
     */
    private ServletContextHandler createHandler(JerseyConfig jersey) {
        ServletContainer container = new ServletContainer(jersey);
        ServletHolder holder = new ServletHolder(container);
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addServlet(holder, "/*");
        return handler;
    }

    public Server getServer() {
        return server;
    }
}
