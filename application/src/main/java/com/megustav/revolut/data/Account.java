package com.megustav.revolut.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Account information
 *
 * @author MeGustav
 * 13/05/2018 00:27
 */
public class Account {

    /** Account number */
    private final String number;
    /** Balance */
    private final BigDecimal balance;
    /** Currency */
    private final Currency currency;

    @JsonCreator
    public Account(@JsonProperty("number") String number,
                   @JsonProperty("balance") BigDecimal balance,
                   @JsonProperty("currency") Currency currency) {
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
        return "Account{" +
                "number='" + number + '\'' +
                ", balance=" + balance +
                ", currency=" + currency +
                '}';
    }
}
