package com.megustav.revolut.database.entity;

import com.megustav.revolut.data.OperationType;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Internal representation of operation
 *
 * @author MeGustav
 * 21/05/2018 23:27
 */
public class InternalOperation {

    /** Id */
    private final long id;
    /** Account id */
    private final long accountId;
    /** Operation type */
    private final OperationType type;
    /** Operation amount */
    private final BigDecimal amount;
    /** Action time */
    private final Date actionTime;

    public InternalOperation(long id,
                             long accountId,
                             OperationType type,
                             BigDecimal amount,
                             Date actionTime) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.actionTime = actionTime;
    }

    public long getId() {
        return id;
    }

    public long getAccountId() {
        return accountId;
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

    @Override
    public String toString() {
        return "InternalOperation{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", type=" + type +
                ", amount=" + amount +
                ", actionTime=" + actionTime +
                '}';
    }
}
