package com.megustav.revolut.database.dao;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.function.Function;

/**
 * Abstract DAO with common methods
 *
 * @author MeGustav
 * 21/05/2018 23:26
 */
public abstract class AbstractDao {

    /** Logger */
    protected static final Logger log = LoggerFactory.getLogger(AbstractDao.class);

    /** Datasource */
    private final DataSource dataSource;

    AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Execute function in transaction
     *
     * @param function function to apply
     * @return execution result
     */
    public <T> T executeInTransaction(Function<Handle, T> function) {
        return executeClosing(handle -> handle.inTransaction(function::apply));
    }

    /**
     * Execute function closing the resource afterwards
     *
     * @param function function to apply
     * @return execution result
     */
    public <T> T executeClosing(Function<Handle, T> function) {
        try (Handle handle = Jdbi.open(dataSource)) {
            return function.apply(handle);
        }
    }

    /**
     * Rollback anything done in transaction
     *
     * @param handle transactional {@link Handle}
     */
    public void rollbackActions(Handle handle) {
        handle.rollback();
    }

}
