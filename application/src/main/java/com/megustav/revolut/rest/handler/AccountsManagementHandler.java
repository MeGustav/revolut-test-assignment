package com.megustav.revolut.rest.handler;

import com.google.inject.Inject;
import com.megustav.revolut.JerseyConfig;
import com.megustav.revolut.database.dao.AccountsDao;
import com.megustav.revolut.database.entity.InternalAccount;
import com.megustav.revolut.rest.MappingUtils;
import com.megustav.revolut.rest.data.AccountPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

/**
 * Accounts handler
 *
 * @author MeGustav
 * 13/05/2018 00:18
 */
@Path("accounts")
public class AccountsManagementHandler implements Handler {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(JerseyConfig.class);
    /** Accounts DAO */
    private final AccountsDao dao;

    @Inject
    public AccountsManagementHandler(AccountsDao dao) {
        this.dao = dao;
    }

    /**
     * Insert new account or update existing
     *
     * While PUT fits here as well (maybe even more appropriate),
     * I feel like choosing POST because that way
     * it does not force us to including account number in the path
     * (whereas PUT ideologically does) which will duplicate the account number
     * passed with the entity.
     * And excluding number from request payload will make it NOT represent
     * entire resource.
     *
     * @param entity account information
     * @return response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postAccount(AccountPayload entity) {
        String number = entity.getNumber();
        log.debug("Got a POST request for account '{}'. Body: {}", number, entity);
        try {
            Optional<InternalAccount> accountOpt = dao.findAccount(number);
            if (accountOpt.isPresent()) {
                // For the sake of the assignment
                // there is not much to update in the account information
                return Response.notModified().entity("Account information could not be modified").build();
            } else {
                boolean inserted = dao.insertAccount(MappingUtils.createBasicAccountInfo(entity));
                return inserted ?
                        Response.created(URI.create("/accounts/" + number)).build() :
                        Response.notModified().entity("Account was not created").build();
            }
        } catch (Exception ex) {
            log.error("Error creating account '{}'", number, ex);
            return Response.serverError().entity("Internal server error").build();
        }
    }

    /**
     * Get account
     *
     * @param number account number
     * @return response
     */
    @GET
    @Path("{account}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("account") String number) {
        log.debug("Got a GET request for account '{}'", number);
        try {
            Optional<InternalAccount> accountOpt = dao.findAccount(number);
            if (accountOpt.isPresent()) {
                InternalAccount account = accountOpt.get();
                log.debug("Found account '{}': {}", number, account);
                return Response.ok()
                        .entity(MappingUtils.createAccountGetResponse(account))
                        .build();
            } else {
                log.debug("Account '{}' was not found", number);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception ex) {
            log.error("Error getting account '{}'", number, ex);
            return Response.serverError().entity("Internal server error").build();
        }
    }

    /**
     * Delete account
     *
     * @param number account number
     * @return response
     */
    @DELETE
    @Path("{account}")
    public Response deleteAccount(@PathParam("account") String number) {
        log.debug("Got a DELETE request for account '{}'", number);
        try {
            Optional<InternalAccount> accountOpt = dao.findAccount(number);
            if (accountOpt.isPresent()) {
                InternalAccount account = accountOpt.get();
                log.debug("Deleting account '{}'...", number);
                boolean deleted = dao.deleteAccount(account.getId());
                return deleted ?
                        Response.ok().build() :
                        Response.notModified().build();
            } else {
                log.debug("Account '{}' was not found", number);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception ex) {
            log.error("Error deleting account '{}'", number, ex);
            return Response.serverError().entity("Internal server error").build();
        }
    }

    @Override
    public String getHandlerName() {
        return "Account management";
    }
}
