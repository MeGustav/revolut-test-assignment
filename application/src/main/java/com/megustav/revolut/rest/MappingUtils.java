package com.megustav.revolut.rest;

import com.megustav.revolut.database.entity.BaseAccountInfo;
import com.megustav.revolut.database.entity.InternalAccount;
import com.megustav.revolut.rest.data.AccountPayload;
import com.megustav.revolut.rest.data.AccountGetResponse;

/**
 * Mapping utils
 *
 * @author MeGustav
 * 17/05/2018 21:47
 */
public final class MappingUtils {

    /**
     * Transform {@link AccountPayload} to {@link BaseAccountInfo}
     *
     * @param data {@link AccountPayload} instance
     * @return {@link BaseAccountInfo} instance
     */
    public static BaseAccountInfo createBasicAccountInfo(AccountPayload data) {
        return new BaseAccountInfo(
                data.getNumber(),
                data.getInitialBalance(),
                data.getCurrency()
        );
    }

    /**
     * Transform {@link InternalAccount} to {@link AccountGetResponse}
     *
     * @param account {@link InternalAccount} instance
     * @return {@link AccountGetResponse} instance
     */

    public static AccountGetResponse createAccountGetResponse(InternalAccount account) {
        return new AccountGetResponse(
                account.getNumber(),
                account.getBalance(),
                account.getCurrency(),
                account.getCreationDate()
        );
    }

    /** Utility class, restricting instantiation */
    private MappingUtils() { }

}
