package com.megustav.revolut.database.entity;

import com.megustav.revolut.data.Currency;
import com.megustav.revolut.rest.data.AccountPayload;

import java.math.BigDecimal;

/**
 * Internal representation of basic account data enough to perform an insert
 * Separating from {@link AccountPayload}
 * because though in this simple app they are the same,
 * in a real app they could differ immensely
 *
 * @author MeGustav
 * 17/05/2018 20:30
 */
public class BaseAccountInfo {

    /** Account number */
    private final String number;
    /** Balance */
    private final BigDecimal balance;
    /** Currency */
    private final Currency currency;

    public BaseAccountInfo(String number, BigDecimal balance, Currency currency) {
        this.number = number;
        this.balance = balance;
        this.currency = currency;
    }

    public String getNumber() {
        return number;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "InternalAccount{" +
                "number='" + number + '\'' +
                ", balance=" + balance +
                ", currency=" + currency +
                '}';
    }
}
