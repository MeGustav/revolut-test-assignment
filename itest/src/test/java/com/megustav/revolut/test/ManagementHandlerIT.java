package com.megustav.revolut.test;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.when;

/**
 * Management handler tests
 *
 * @author MeGustav
 * 05/05/2018 20:00
 */
public class ManagementHandlerIT {

    /**
     * Setting up server port for tests
     *
     * @param port server port set up in config
     */
    @Parameters({"server.port"})
    @BeforeClass
    public void beforeTest(int port) {
        RestAssured.port = port;
        RestAssured.basePath = "management";
    }

    /**
     * Testing health check.
     *
     * It is obviously is already checked because
     * test won't even start without healthcheck.
     * But testing it nevertheless just for the sake of it
     */
    @Test
    public void testHealthcheck() {
        when().get("health").then().statusCode(Response.Status.OK.getStatusCode());
    }

}
