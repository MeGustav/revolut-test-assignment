package com.megustav.revolut.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.megustav.revolut.configuration.ApplicationConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * Module providing parsed configuration
 *
 * @author MeGustav
 * 26/04/2018 20:01
 */
public class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() { }

    /**
     * @return parsed application configuration
     */
    @Provides
    private ApplicationConfiguration providesConfiguration() throws ConfigurationException {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties().setFileName("application.properties"));
        FileBasedConfiguration configuration = builder.getConfiguration();
        return new ApplicationConfiguration(configuration.getInt("server.port"));
    }
}
