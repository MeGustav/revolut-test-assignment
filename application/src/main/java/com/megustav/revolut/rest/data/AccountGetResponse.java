package com.megustav.revolut.rest.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.megustav.revolut.data.Currency;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Account data response
 *
 * @author MeGustav
 * 13/05/2018 00:27
 */
public class AccountGetResponse {

    /** Account number */
    @JsonProperty(value = "number", required = true)
    private final String number;
    /** Balance */
    @JsonProperty(value = "current-balance", required = true)
    private final BigDecimal currentBalance;
    /** Currency */
    @JsonProperty(value = "currency", required = true)
    private final Currency currency;
    /** Account creation date */
    @JsonProperty(value = "creation-date", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final Date creationDate;

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
     * @param currentBalance initial balance
     * @param currency account currency
     * @param creationDate account creation date
     */
    @JsonCreator
    public AccountGetResponse(
            @JsonProperty(value = "number", required = true) String number,
            @JsonProperty(value = "current-balance", required = true) BigDecimal currentBalance,
            @JsonProperty(value = "currency", required = true) Currency currency,
            @JsonProperty(value = "creation-date", required = true) Date creationDate) {
        this.number = number;
        this.currentBalance = currentBalance;
        this.currency = currency;
        this.creationDate = creationDate;
    }

    public String getNumber() {
        return number;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public String toString() {
        return "Account{" +
                "number='" + number + '\'' +
                ", balance=" + currentBalance +
                ", currency=" + currency +
                '}';
    }
}
