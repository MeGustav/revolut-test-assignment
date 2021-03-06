package com.megustav.revolut.rest.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.megustav.revolut.data.Currency;
import com.megustav.revolut.data.OperationType;

import java.math.BigDecimal;

/**
 * Operation payload
 *
 * @author MeGustav
 * 15/05/2018 22:51
 */
public class OperationPayload {

    /** Operation type */
    @JsonProperty(value = "type", required = true)
    private final OperationType type;
    /** Operation amount */
    @JsonProperty(value = "amount", required = true)
    private final BigDecimal amount;
    /**
     * Operation currency.
     * Added to prevent some accidental operations to go through
     */
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
     * @param type operation type
     * @param amount operation amount
     * @param currency operation currency
     */
    @JsonCreator
    public OperationPayload(
            @JsonProperty(value = "type", required = true) OperationType type,
            @JsonProperty(value = "amount", required = true) BigDecimal amount,
            @JsonProperty(value = "currency", required = true) Currency currency) {
        this.type = type;
        this.amount = amount;
        this.currency = currency;
    }

    public OperationType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "type=" + type +
                ", amount=" + amount +
                ", currency=" + currency +
                '}';
    }
}
