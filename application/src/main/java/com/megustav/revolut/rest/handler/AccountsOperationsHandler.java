package com.megustav.revolut.rest.handler;

import com.google.inject.Inject;
import com.megustav.revolut.JerseyConfig;
import com.megustav.revolut.database.dao.AccountsDao;
import com.megustav.revolut.database.entity.InternalAccount;
import com.megustav.revolut.database.entity.InternalOperation;
import com.megustav.revolut.rest.MappingUtils;
import com.megustav.revolut.rest.data.OperationPayload;
import com.megustav.revolut.rest.processor.OperationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

/**
 * Operations handler
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
    /** Operation processor */
    private final OperationProcessor operationProcessor;

    @Inject
    public AccountsOperationsHandler(AccountsDao dao, OperationProcessor operationProcessor) {
        this.dao = dao;
        this.operationProcessor = operationProcessor;
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
        log.debug("Got POST request with operation {} on account '{}'", operation, number);
        return operationProcessor.processOperation(number, operation);
    }

    /**
     * Get all account operations
     *
     * @param number account number
     * @return account operations
     */
    @GET
    @Path("{account}/operations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperations(@PathParam("account") String number) {
        log.debug("Got GET request for account '{}' operations", number);
        Optional<InternalAccount> accountOpt = dao.findAccount(number);
        if (! accountOpt.isPresent()) {
            log.debug("Account '{}' was not found", number);
            throw new NotFoundException("Account was not found");
        }
        List<InternalOperation> operations = dao.getOperations(accountOpt.get().getId());
        return Response.ok().entity(MappingUtils.createOperationsGetResponse(number, operations)).build();
    }

    @Override
    public String getHandlerName() {
        return "Account operations";
    }
}
