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
    /** Database url */
    private final String databaseUrl;
    /** Database username */
    private final String databaseUser;
    /** Database password */
    private final String databasePassword;

    public ApplicationConfiguration(int serverPort,
                                    String databaseUrl,
                                    String databaseUser,
                                    String databasePassword) {
        this.serverPort = serverPort;
        this.databaseUrl = databaseUrl;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }
}
