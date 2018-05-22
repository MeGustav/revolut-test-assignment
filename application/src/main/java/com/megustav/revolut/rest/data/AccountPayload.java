package com.megustav.revolut.rest.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.megustav.revolut.data.Currency;

import java.math.BigDecimal;

/**
 * Account representation
 *
 * @author MeGustav
 * 13/05/2018 00:27
 */
public class AccountPayload {

    /** Account number */
    @JsonProperty(value = "number", required = true)
    private final String number;
    /** Balance */
    @JsonProperty(value = "initial-balance", required = true)
    private final BigDecimal initialBalance;
    /** Currency */
    @JsonProperty(value = "currency", required = true)
    private final Currency currency;

    /**
     * Construct an instance
     *
     * Using {@link JsonCreator} to ensure immutability,
     * but it requires {@link JsonProperty} for every field
     *
     * Duplicating {@link JsonProperty} fully (even the {@link JsonProperty#required()})
     * in constructor for now so that both serialization and deserialization work fine.
     * TODO there is obviously a better way of doing this, should find out
     *
     * @param number account number
     * @param initialBalance initial balance
     * @param currency account currency
     */
    @JsonCreator
    public AccountPayload(
            @JsonProperty(value = "number", required = true) String number,
            @JsonProperty(value = "initial-balance", required = true) BigDecimal initialBalance,
            @JsonProperty(value = "currency", required = true) Currency currency) {
        this.number = number;
        this.initialBalance = initialBalance;
        this.currency = currency;
    }

    public String getNumber() {
        return number;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "Account{" +
                "number='" + number + '\'' +
                ", balance=" + initialBalance +
                ", currency=" + currency +
                '}';
    }
}
