package com.megustav.revolut.handler;

import com.google.inject.Singleton;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Management handler
 *
 * @author MeGustav
 * 05/05/2018 17:48
 */
@Path("management")
@Singleton
public class ManagementHandler implements Handler {

    @GET
    @Path("health")
    public Response health() {
        return Response.ok().build();
    }

    @Override
    public String getHandlerName() {
        return "Management";
    }
}
