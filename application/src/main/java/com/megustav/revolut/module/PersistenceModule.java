package com.megustav.revolut.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.megustav.revolut.configuration.ApplicationConfiguration;
import org.h2.jdbcx.JdbcConnectionPool;

import javax.sql.DataSource;

/**
 * Module providing datasource
 *
 * @author MeGustav
 * 10/05/2018 21:43
 */
public class PersistenceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DataSource.class).to(JdbcConnectionPool.class);
    }

    /**
     * @return H2 datasource
     */
    @Provides
    @Singleton
    public JdbcConnectionPool providesDataSource(ApplicationConfiguration configuration) {
        return JdbcConnectionPool.create(
                configuration.getDatabaseUrl(),
                configuration.getDatabaseUser(),
                configuration.getDatabasePassword()
        );
    }

}
