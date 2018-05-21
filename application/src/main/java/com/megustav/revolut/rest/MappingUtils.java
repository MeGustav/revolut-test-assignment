package com.megustav.revolut.rest;

import com.megustav.revolut.database.entity.BaseAccountInfo;
import com.megustav.revolut.database.entity.InternalAccount;
import com.megustav.revolut.database.entity.InternalOperation;
import com.megustav.revolut.rest.data.AccountGetResponse;
import com.megustav.revolut.rest.data.AccountPayload;
import com.megustav.revolut.rest.data.OperationsGetResponse;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Transform list of {@link InternalOperation} to {@link OperationsGetResponse}
     *
     * @param account account number
     * @param operations list of {@link InternalOperation}
     * @return {@link OperationsGetResponse} instance
     */
    public static OperationsGetResponse createOperationsGetResponse(String account,
                                                                    List<InternalOperation> operations) {
        return new OperationsGetResponse(
                operations.stream()
                        .map(op -> new OperationsGetResponse.OperationInfo(
                                account, op.getType(), op.getAmount(), op.getActionTime()))
                        .collect(Collectors.toList())
        );
    }

    /** Utility class, restricting instantiation */
    private MappingUtils() { }

}
