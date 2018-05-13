package com.megustav.revolut.database.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.megustav.revolut.JerseyConfig;
import com.megustav.revolut.data.Account;
import com.megustav.revolut.data.Currency;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Optional;

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
            "(number, balance, currency) VALUES " +
            "(?, ?, ?)";

    /**
     * Insert account
     *
     * @param account account
     */
    public void insertAccount(Account account) {
        Jdbi.open(dataSource).execute(INSERT_ACCOUNT,
                account.getNumber(), account.getBalance(), account.getCurrency().getNumericCode()
        );
    }

    /** Update account */
    private static final String UPDATE_ACCOUNT = "UPDATE accounts " +
            "SET balance = ?, currency = ? WHERE number = ?";

    /**
     * Update account
     *
     * @param account account
     * @return whether or not account was updated
     */
    public boolean updateAccount(Account account) {
        return Jdbi.open(dataSource).createUpdate(UPDATE_ACCOUNT)
                .bind(0, account.getBalance())
                .bind(1, account.getCurrency().getNumericCode())
                .bind(2, account.getNumber())
                .execute() > 0;

    }

    /** Find account */
    private static final String FIND_ACCOUNT =
            "SELECT number, balance, currency FROM accounts WHERE number = ?";

    /**
     * Find account
     *
     * @param number account number
     * @return {@link Optional} account
     */
    public Optional<Account> findAccount(String number) {
        return Jdbi.open(dataSource).select(FIND_ACCOUNT, number)
                .map((rs, ctx) -> new Account(
                        rs.getString("number"),
                        rs.getBigDecimal("balance"),
                        Currency.of(rs.getString("currency"))))
                .findFirst();
    }
    
}
