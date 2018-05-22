package com.megustav.revolut.test;

import com.megustav.revolut.rest.data.AccountGetResponse;
import com.megustav.revolut.rest.data.AccountPayload;
import com.megustav.revolut.data.Currency;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;

/**
 * Accounts handler tests
 *
 * TODO ideally there should also be some basic raw JSON-payload tests
 *
 * @author MeGustav
 * 05/05/2018 20:00
 */
public class AccountsManagementHandlerIT {

    /**
     * Setting up server port for tests
     *
     * @param port server port set up in config
     */
    @Parameters({"server.port"})
    @BeforeClass
    public void beforeTest(int port) {
        RestAssured.port = port;
        RestAssured.basePath = "accounts";
    }

    /**
     * Test getting non existent account
     */
    @Test
    public void testNoAccountFound() {
        requestAccountGet("42307810990000000003")
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Test creating account
     */
    @Test
    public void testAccountCreation() {
        AccountPayload account = new AccountPayload("42307810990000000001", BigDecimal.TEN, Currency.RUR);

        requestAccountPost(account)
                .statusCode(Status.CREATED.getStatusCode());
        AccountGetResponse foundAccount = requestGuaranteedAccountGet(account.getNumber());

        Assertions.assertThat(foundAccount.getCurrentBalance())
                .as("Amount")
                .isEqualByComparingTo(account.getInitialBalance());
        Assertions.assertThat(foundAccount.getCurrency())
                .as("Currency")
                .isEqualTo(account.getCurrency());
    }

    /**
     * Test creating without any required field.
     * There is no way of creating a good invalid data from java,
     * so getting a raw json from file
     */
    @Test
    public void testAccountCreationWithNoCurrency() throws IOException {
        InputStream json = this.getClass()
                .getResourceAsStream("/data/account-payload-no-currency.json");
        given()
                .body(IOUtils.toString(json, StandardCharsets.UTF_8))
                .header(new Header("Content-Type", ContentType.JSON.toString()))
                .when()
                .post()
                .then().statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Make an account POST request
     *
     * @param account account
     * @return response
     */
    private ValidatableResponse requestAccountPost(AccountPayload account) {
        return given()
                .body(account)
                .header(new Header("Content-Type", ContentType.JSON.toString()))
                .when()
                .post()
                .then();
    }

    /**
     * Make an existing account GET request
     *
     * @param number account number
     * @return account data
     */
    private AccountGetResponse requestGuaranteedAccountGet(String number) {
        return requestAccountGet(number)
                .statusCode(Status.OK.getStatusCode())
                .extract().as(AccountGetResponse.class);
    }

    /**
     * Make an account GET request
     *
     * @param number account number
     * @return response
     */
    private ValidatableResponse requestAccountGet(String number) {
        return given()
                .when()
                .get(number)
                .then();
    }

}
