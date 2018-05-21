package com.megustav.revolut.database.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.megustav.revolut.data.Currency;
import com.megustav.revolut.data.OperationType;
import com.megustav.revolut.database.entity.BaseAccountInfo;
import com.megustav.revolut.database.entity.InternalAccount;
import com.megustav.revolut.database.entity.InternalOperation;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.RowMapper;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Accounts data access
 *
 * @author MeGustav
 * 13/05/2018 00:38
 */
@Singleton
public class AccountsDao extends AbstractDao {

    @Inject
    public AccountsDao(DataSource dataSource) {
        super(dataSource);
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
        return executeClosing(handle -> {
            boolean inserted = handle.execute(INSERT_ACCOUNT,
                    account.getNumber(),
                    account.getBalance(),
                    account.getCurrency().getNumericCode(),
                    new Date()) > 0;
            log.debug("Account {} was{} inserted", account.getNumber(), inserted ? "" : " not");
            return inserted;
        });
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
        return executeInTransaction(handle -> {
            handle.execute(DELETE_TRANSACTIONS, id);
            boolean deleted = handle.execute(DELETE_ACCOUNT, id) > 0;
            log.debug("Account {} was{} deleted", id, deleted ? "" : " not");
            return deleted;
        });
    }

    /** Account row mapper */
    private static final RowMapper<InternalAccount> ACCOUNT_MAPPER = (rs, ctx) -> new InternalAccount(
            rs.getLong("id"),
            rs.getString("number"),
            rs.getBigDecimal("balance"),
            Currency.of(rs.getString("currency")),
            rs.getDate("creation_date"));

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
        return executeClosing(handle -> handle.select(FIND_ACCOUNT, number)
                .map(ACCOUNT_MAPPER)
                .findFirst());
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
        return executeClosing(handle -> handle.select(GET_ACCOUNT, id)
                .map(ACCOUNT_MAPPER)
                .findFirst());
    }

    /** Operation row mapper */
    private static final RowMapper<InternalOperation> OPERATION_MAPPER = (rs, ctx) -> new InternalOperation(
            rs.getLong("id"),
            rs.getLong("account_id"),
            OperationType.of(rs.getInt("type")),
            rs.getBigDecimal("amount"),
            rs.getTimestamp("action_time"));

    /** Get all account operations */
    private static final String GET_OPERATIONS =
            "SELECT id, account_id, type, amount, action_time FROM operations WHERE account_id = ?";

    /**
     * Get account operations
     *
     * @param accountId account id
     * @return list of account operations
     */
    public List<InternalOperation> getOperations(long accountId) {
        log.debug("Getting account {} operations...", accountId);
        return executeClosing(handle -> handle.select(GET_OPERATIONS, accountId)
                .map(OPERATION_MAPPER)
                .collect(Collectors.toList()));
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
        return executeClosing(handle -> updateBalance(handle, accountId, balance));
    }

    /**
     * Update account balance
     *
     * @param accountId account id
     * @param balance balance
     * @return {@code true} if balance was updated, {@code false} otherwise
     */
    public boolean updateBalance(Handle handle, long accountId, BigDecimal balance) {
        log.debug("Updating balance of account {}", accountId);
        boolean updated = handle.execute(UPDATE_BALANCE, balance, accountId) > 0;
        log.debug("Account {} balance was{} updated", accountId, updated ? "" : " not");
        return updated;
    }

    /** Operation insertion */
    private static final String INSERT_OPERATION = "INSERT INTO operations " +
            "(account_id, type, amount, action_time) VALUES (?, ?, ?, ?)";

    /**
     * Insert operation
     *
     * @param accountId account id
     * @param type operation type
     * @param amount amount
     * @return {@code true} if operation was inserted, {@code false} otherwise
     */
    public boolean insertOperation(long accountId, OperationType type, BigDecimal amount) {
        return executeClosing(handle -> insertOperation(handle, accountId, type, amount));
    }

    /**
     * Insert operation
     *
     * @param accountId account id
     * @param type operation type
     * @param amount amount
     * @return {@code true} if operation was inserted, {@code false} otherwise
     */
    public boolean insertOperation(Handle handle, long accountId, OperationType type, BigDecimal amount) {
        log.debug("Inserting {} operation of {} on account {}", type, amount, accountId);
        boolean inserted = handle.execute(INSERT_OPERATION, accountId, type.getCode(), amount, new Date()) > 0;
        log.debug("Operation on account {} was{} inserted", accountId, inserted ? "" : " not");
        return inserted;
    }

}
