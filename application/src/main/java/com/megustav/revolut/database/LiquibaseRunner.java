package com.megustav.revolut.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class for applying liquibase change log
 *
 * @author MeGustav
 * 10/05/2018 22:33
 */
@Singleton
public class LiquibaseRunner {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(LiquibaseRunner.class);

    /** Datasource */
    private final DataSource dataSource;

    @Inject
    public LiquibaseRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Apply change log
     */
    public void applyChangeLog() throws SQLException, LiquibaseException {
        log.info("Applying liquibase change log...");
        try (Connection connection = dataSource.getConnection()) {
            Database db = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new liquibase.Liquibase(
                    "db/changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    db
            );
            liquibase.update(new Contexts());
        }
        log.info("Change log applied");
    }

}
