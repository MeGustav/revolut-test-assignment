package com.megustav.revolut.rest.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.megustav.revolut.data.OperationType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Operations data response
 *
 * @author MeGustav
 * 22/05/2018 00:11
 */
public class OperationsGetResponse {

    /** Operations */
    @JsonProperty(value = "operations", required = true)
    private final List<OperationInfo> operations;

    @JsonCreator
    public OperationsGetResponse(
            @JsonProperty(value = "operations", required = true) List<OperationInfo> operations) {
        this.operations = operations;
    }

    public List<OperationInfo> getOperations() {
        return operations;
    }

    /** Operation info */
    public static class OperationInfo {

        /** Account number */
        @JsonProperty(value = "account", required = true)
        private final String account;
        /** Operation type */
        @JsonProperty(value = "type", required = true)
        private final OperationType type;
        /** Operation amount */
        @JsonProperty(value = "amount", required = true)
        private final BigDecimal amount;
        /** Operation action time */
        @JsonProperty(value = "action-time", required = true)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private final Date actionTime;

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
         * @param account account
         * @param type operation type
         * @param amount operation amount
         * @param actionTime action time
         */
        @JsonCreator
        public OperationInfo(
                @JsonProperty(value = "account", required = true) String account,
                @JsonProperty(value = "type", required = true) OperationType type,
                @JsonProperty(value = "amount", required = true) BigDecimal amount,
                @JsonProperty(value = "action-time", required = true) Date actionTime) {
            this.account = account;
            this.type = type;
            this.amount = amount;
            this.actionTime = actionTime;
        }

        public String getAccount() {
            return account;
        }

        public OperationType getType() {
            return type;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public Date getActionTime() {
            return actionTime;
        }
    }

}
