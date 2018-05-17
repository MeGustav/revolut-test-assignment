package com.megustav.revolut.database.entity;

import com.megustav.revolut.data.Currency;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Internal representation of existing account (with id)
 *
 * Could go with just one entity {@link BaseAccountInfo} just with
 * optional fields, but this way there is less room for a
 * programming error while using the entity.
 *
 * Using public constructor for the sake of this assignment,
 * but with growing number of fields could rewrite to builder
 *
 * @author MeGustav
 * 17/05/2018 20:30
 */
public class InternalAccount extends BaseAccountInfo {

    /** Internal id */
    private final long id;
    /** Account creation date */
    private final Date creationDate;

    public InternalAccount(long id,
                           String number,
                           BigDecimal balance,
                           Currency currency,
                           Date creationDate) {
        super(number, balance, currency);
        this.id = id;
        this.creationDate = creationDate;
    }

    public InternalAccount(long id, Date creationDate, BaseAccountInfo account) {
        super(account.getNumber(), account.getBalance(), account.getCurrency());
        this.id = id;
        this.creationDate = creationDate;
    }

    public long getId() {
        return id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public String toString() {
        return "InternalAccount{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                "} " + super.toString();
    }
}
