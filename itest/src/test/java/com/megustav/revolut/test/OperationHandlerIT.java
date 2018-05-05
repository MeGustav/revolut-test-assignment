package com.megustav.revolut.test;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.when;
import static org.hamcrest.core.Is.is;

/**
 * Operations handler tests
 *
 * @author MeGustav
 * 05/05/2018 20:00
 */
public class OperationHandlerIT {

    /**
     * Setting up server port for tests
     *
     * @param port server port set up in config
     */
    @Parameters({"server.port"})
    @BeforeClass
    public void beforeTest(int port) {
        RestAssured.port = port;
        RestAssured.basePath = "operation";
    }

    /**
     * Testing withdrawal
     */
    @Test
    public void testWithdrawal() {
        when().post("withdrawal").then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("code", is(0))
                .body("description", is("OK"));
    }

}
