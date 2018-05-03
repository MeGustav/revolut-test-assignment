package com.megustav.revolut.configuration;

/**
 * Application configuration
 *
 * @author MeGustav
 * 29/04/2018 15:40
 */
public class ApplicationConfiguration {

    /** Server port */
    private final int serverPort;

    public ApplicationConfiguration(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getServerPort() {
        return serverPort;
    }
}
