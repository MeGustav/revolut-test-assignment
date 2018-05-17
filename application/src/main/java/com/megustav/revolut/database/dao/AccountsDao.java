package com.megustav.revolut.database.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.megustav.revolut.JerseyConfig;
import com.megustav.revolut.data.Currency;
import com.megustav.revolut.data.OperationType;
import com.megustav.revolut.database.entity.BaseAccountInfo;
import com.megustav.revolut.database.entity.InternalAccount;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

/**
 * Accounts data access
 *
 * @author MeGustav
 * 13/05/2018 00:38
 */
@Singleton
public class AccountsDao {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(JerseyConfig.class);

    /** Datasource */
    private final DataSource dataSource;

    @Inject
    public AccountsDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Insert account */
    private static final String INSERT_ACCOUNT = "INSERT INTO accounts " +
            "(number, balance, currency, creation_date) VALUES " +
            "(?, ?, ?, ?)";

    /**
     * Insert account
     *
     * @param account account
     */
    public boolean insertAccount(BaseAccountInfo account) {
        log.debug("Inserting account: {}", account);
        boolean inserted = Jdbi.open(dataSource).execute(INSERT_ACCOUNT,
                account.getNumber(),
                account.getBalance(),
                account.getCurrency().getNumericCode(),
                new Date()) > 0;
        log.debug("Account was{} inserted", account.getNumber(), inserted ? "" : " not");
        return inserted;
    }

    /** Delete account */
    private static final String DELETE_ACCOUNT =
            "DELETE FROM accounts WHERE id = ?";
    /** Delete account transactions */
    private static final String DELETE_TRANSACTIONS =
            "DELETE FROM operations WHERE account_id = ?";

    /**
     * Delete account and it's transactions
     * @param id account id
     * @return {@code true} if operation was deleted, {@code false} otherwise
     */
    public boolean deleteAccount(long id) {
        log.debug("Deleting account: {}", id);
        return Jdbi.open(dataSource).inTransaction(handle -> {
            handle.execute(DELETE_TRANSACTIONS, id);
            boolean deleted = handle.execute(DELETE_ACCOUNT, id) > 0;
            log.debug("Account {} was{} deleted", id, deleted ? "" : " not");
            return deleted;
        });
    }

    /** Find account */
    private static final String FIND_ACCOUNT =
            "SELECT id, number, balance, currency, creation_date FROM accounts WHERE number = ?";

    /**
     * Find account
     *
     * @param number account number
     * @return {@link Optional} account
     */
    public Optional<InternalAccount> findAccount(String number) {
        log.debug("Searching account with number: '{}'", number);
        return Jdbi.open(dataSource).select(FIND_ACCOUNT, number)
                .map(new AccountRowMapper())
                .findFirst();
    }

    /** Find account */
    private static final String GET_ACCOUNT =
            "SELECT id, number, balance, currency, creation_date FROM accounts WHERE id = ?";


    /**
     * Get account
     *
     * @param id account id
     * @return {@link Optional} account
     */
    public Optional<InternalAccount> getAccount(long id) {
        log.debug("Searching account with id: '{}'", id);
        return Jdbi.open(dataSource).select(GET_ACCOUNT, id)
                .map(new AccountRowMapper())
                .findFirst();
    }

    /** Account balance update */
    private static final String UPDATE_BALANCE = "UPDATE accounts " +
            "SET balance = ? WHERE id = ?";

    /**
     * Update account balance
     *
     * @param accountId account id
     * @param balance account balance
     * @return {@code true} if balance was updated, {@code false} otherwise
     */
    public boolean updateBalance(long accountId, BigDecimal balance) {
        return updateBalance(null, accountId, balance);
    }

    /**
     * Update account balance
     *
     * @param existing {@link Handle} instance which is used to execute operation with
     * @param accountId account id
     * @param balance balance
     * @return {@code true} if balance was updated, {@code false} otherwise
     */
    public boolean updateBalance(Handle existing, long accountId, BigDecimal balance) {
        log.debug("Updating balance of account {}", accountId);
        Handle handle = existing == null ? Jdbi.open(dataSource) : existing;
        boolean updated = handle.execute(UPDATE_BALANCE, balance, accountId) > 0;
        log.debug("Account {} balance was{} updated", accountId, updated ? "" : " not");
        return updated;
    }

    /** Operation insertion */
    private static final String INSERT_OPERATION = "INSERT INTO accounts " +
            "(account_id, type, amount, action_time) VALUES (?, ?, ?, ?, ?)";

    /**
     * Insert operation
     *
     * @param accountId account id
     * @param type operation type
     * @param amount amount
     * @return {@code true} if operation was inserted, {@code false} otherwise
     */
    public boolean insertOperation(long accountId, OperationType type, BigDecimal amount) {
        return insertOperation(null, accountId, type, amount);
    }

    /**
     * Insert operation
     *
     * @param existing {@link Handle} instance which is used to execute operation with
     * @param accountId account id
     * @param type operation type
     * @param amount amount
     * @return {@code true} if operation was inserted, {@code false} otherwise
     */
    public boolean insertOperation(Handle existing, long accountId, OperationType type, BigDecimal amount) {
        log.debug("Inserting {} operation of {} on account {}", type, amount, accountId);
        Handle handle = existing == null ? Jdbi.open(dataSource) : existing;
        boolean inserted = handle.execute(INSERT_OPERATION, accountId, type.getCode(), amount, new Date()) > 0;
        log.debug("Operation on account {} was{} inserted", accountId, inserted ? "" : " not");
        return inserted;
    }

    /**
     * Execute operations in transaction
     *
     * @param operations operations to apply
     * @return execution result
     */
    public <T> T executeInTransaction(Function<Handle, T> operations) {
        return Jdbi.open(dataSource).inTransaction(operations::apply);
    }

    /**
     * Rollback anything done in transaction
     *
     * @param handle transactional {@link Handle}
     */
    public void rollbackActions(Handle handle) {
        handle.rollback();
    }

    /**
     * Account row mapper
     */
    private static class AccountRowMapper implements RowMapper<InternalAccount> {

        @Override
        public InternalAccount map(ResultSet rs, StatementContext ctx) throws SQLException {
            return new InternalAccount(
                    rs.getLong("id"),
                    rs.getString("number"),
                    rs.getBigDecimal("balance"),
                    Currency.of(rs.getString("currency")),
                    rs.getDate("creation_date"));
        }
    }
}
