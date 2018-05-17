package com.megustav.revolut.rest.handler;

import com.google.inject.Inject;
import com.megustav.revolut.JerseyConfig;
import com.megustav.revolut.data.OperationType;
import com.megustav.revolut.database.dao.AccountsDao;
import com.megustav.revolut.database.entity.InternalAccount;
import com.megustav.revolut.rest.data.OperationPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Operations handler
 *
 * Maybe this class became to be too bulky and there should be
 * another abstraction incapsulating most business logic,
 * but at this moment
 *
 *
 * @author MeGustav
 * 13/05/2018 00:18
 */
@Path("accounts")
public class AccountsOperationsHandler implements Handler {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(JerseyConfig.class);
    /** Accounts DAO */
    private final AccountsDao dao;

    @Inject
    public AccountsOperationsHandler(AccountsDao dao) {
        this.dao = dao;
    }

    /**
     * Perform an operation on the account.
     *
     * Using for instance {@link Response.Status#CONFLICT} for insufficient funds.
     * Not a big fan of representing business exceptions as HTTP codes,
     * but it does really seem to fit.
     *
     * TODO describe return codes
     *
     * @param number account number
     * @param operation operation payload
     * @return response
     */
    @POST
    @Path("{account}/operation")
    public Response performOperation(@PathParam("account") String number, OperationPayload operation) {
        try {
            log.debug("Got POST request with operation {} on account '{}'", operation, number);
            BigDecimal amount = operation.getAmount();
            if (amount == null || BigDecimal.ZERO.compareTo(amount) > 0) {
                log.debug("Invalid amount: {}", amount);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid amount").build();
            }
            Optional<InternalAccount> accountOpt = dao.findAccount(number);
            if (! accountOpt.isPresent()) {
                log.debug("Account '{}' was not found", number);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            InternalAccount account = accountOpt.get();
            if (account.getCurrency() != operation.getCurrency()) {
                log.debug("Operation and account currencies differ. Account: {}, operation: {}",
                        account.getCurrency(), operation.getCurrency()
                );
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Operation and account currencies differ").build();
            }
            if (operation.getType() == OperationType.WITHDRAWAL) {
                BigDecimal remainder = account.getBalance().subtract(operation.getAmount());
                if (BigDecimal.ZERO.compareTo(remainder) > 0) {
                    log.debug("Insufficient funds on account '{}' for withdrawal of {}", number, operation.getAmount());
                    return Response.status(Response.Status.CONFLICT)
                            .entity("Insufficient funds").build();
                }
            }
            return performOperation(account.getId(), operation.getType(), operation.getAmount());
        } catch (Exception ex) {
            log.error("Error processing operation on account '{}': {}", number, operation, ex);
            return Response.serverError().entity("Internal server error").build();
        }
    }

    /**
     * Perform an operation
     *
     * @param accountId account id
     * @param type operation type
     * @param amount operation amount
     * @return response
     */
    private Response performOperation(long accountId, OperationType type, BigDecimal amount) {
        // TODO check the concurrency constraints
        return dao.executeInTransaction(handle -> {
            // Getting the account again to get the latest data
            Optional<InternalAccount> accountOpt = dao.getAccount(accountId);
            if (! accountOpt.isPresent()) {
                log.debug("Account {} was deleted. Aborting operation", accountId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Account was deleted").build();
            }

            boolean operationInserted = dao.insertOperation(handle, accountId, type, amount);
            if (! operationInserted) {
                log.debug("Operation on account {} was not applied", accountId);
                return Response.notModified("Operation was not applied").build();
            }

            InternalAccount account = accountOpt.get();
            BigDecimal newBalance = type == OperationType.WITHDRAWAL ?
                    account.getBalance().subtract(amount) :
                    account.getBalance().add(amount);
            boolean balanceUpdated = dao.updateBalance(handle, account.getId(), newBalance);
            if (balanceUpdated) {
                log.debug("Operation successfully applied");
                return Response.ok().build();
            } else {
                log.debug("Operation on account processed but account was not updated, rolling transaction back");
                dao.rollbackActions(handle);
                return Response.notModified().entity("Balance was not updated").build();
            }
        });
    }

    @Override
    public String getHandlerName() {
        return "Account operations";
    }
}
