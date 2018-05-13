package com.megustav.revolut.handler;

import com.google.inject.Inject;
import com.megustav.revolut.JerseyConfig;
import com.megustav.revolut.data.Account;
import com.megustav.revolut.database.dao.AccountsDao;
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
public class AccountsHandler implements Handler {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(JerseyConfig.class);

    private final AccountsDao dao;

    @Inject
    public AccountsHandler(AccountsDao dao) {
        this.dao = dao;
    }

    /**
     * Insert new account or update existing
     *
     * PUT supports both create and update
     * Could as well use POST here, but
     * decided to use PUT because of account number uniquely
     * identifying the resource, so could be conveniently used as path param
     *
     * @param number account number
     * @param entity account information
     *               Though we have to set account both
     *               in path and body, it's much better
     *               than extracting the rest of the fields
     *               in a separate entity (like Balance, containing just
     *               amount and currency), because with hypothetical
     *               increase of account fields it will get painful to maintain
     * @return response
     */
    @PUT
    @Path("{account}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(@PathParam("account") String number, Account entity) {
        log.debug("Got a PUT request for account '{}'. Body: {}", number, entity);
        if (! number.equals(entity.getNumber())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Path and entity account numbers don't match")
                    .build();
        }
        try {
            Optional<Account> accountOpt = dao.findAccount(number);
            if (accountOpt.isPresent()) {
                boolean updated = dao.updateAccount(entity);
                log.debug("Account '{}' was {} updated", number, updated ? "successfully" : "not");
                return updated ?
                        Response.ok().build() :
                        Response.notModified().build();
            } else {
                dao.insertAccount(entity);
                log.debug("Account was '{}' successfully created", number);
                return Response.created(URI.create("/accounts/" + number)).build();
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
            Optional<Account> accountOpt = dao.findAccount(number);
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                log.debug("Found account '{}': {}", number, account);
                return Response.ok().entity(account).build();
            } else {
                log.debug("Account '{}' was not found", number);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception ex) {
            log.error("Error getting account '{}'", number, ex);
            return Response.serverError().entity("Internal server error").build();
        }
    }

    @Override
    public String getHandlerName() {
        return "Accounts";
    }
}
