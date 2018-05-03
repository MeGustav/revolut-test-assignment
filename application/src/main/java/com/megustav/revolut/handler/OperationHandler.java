package com.megustav.revolut.handler;

import com.google.inject.Singleton;
import com.megustav.revolut.data.OperationResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Account operations handler
 *
 * @author MeGustav
 * 26/04/2018 21:32
 */
@Path("operation")
@Singleton
public class OperationHandler {

    @GET
    @Path("withdrawal")
    @Produces(MediaType.APPLICATION_JSON)
    public OperationResponse withdrawal() {
        return new OperationResponse(0, "OK");
    }

}
