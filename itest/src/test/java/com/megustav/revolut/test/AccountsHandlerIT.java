package com.megustav.revolut.test;

import com.megustav.revolut.data.Account;
import com.megustav.revolut.data.Currency;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response.Status;
import java.math.BigDecimal;

import static io.restassured.RestAssured.given;

/**
 * Accounts handler tests
 *
 * @author MeGustav
 * 05/05/2018 20:00
 */
public class AccountsHandlerIT {

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
        Account account = new Account("42307810990000000001", BigDecimal.TEN, Currency.RUR);

        requestAccountPut(account.getNumber(), account)
                .statusCode(Status.CREATED.getStatusCode());
        Account foundAccount = requestGuaranteedAccountGet(account.getNumber());

        Assertions.assertThat(foundAccount.getBalance())
                .as("Amount")
                .isEqualByComparingTo(account.getBalance());
        Assertions.assertThat(foundAccount.getCurrency())
                .as("Currency")
                .isEqualTo(account.getCurrency());
    }

    /**
     * Test creation with request number doesn't match the path param
     */
    @Test
    public void testAccountCreationNumbersNotMatch() {
        Account account = new Account("42307810990000000004", BigDecimal.TEN, Currency.RUR);
        requestAccountPut("42307810990000000005", account)
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Tesdt account update
     */
    @Test
    public void testAccountUpdate() {
        String number = "42307810990000000002";
        Account account = new Account(number, BigDecimal.TEN, Currency.RUR);
        requestAccountPut(number, account)
                .statusCode(Status.CREATED.getStatusCode());
        Account changedAccount = new Account(number, new BigDecimal("200.00"), Currency.RUR);
        requestAccountPut(number, changedAccount)
                .statusCode(Status.OK.getStatusCode());

        Account foundAccount = requestGuaranteedAccountGet(number);

        Assertions.assertThat(foundAccount.getBalance())
                .as("Amount")
                .isEqualByComparingTo(changedAccount.getBalance());
        Assertions.assertThat(foundAccount.getCurrency())
                .as("Currency")
                .isEqualTo(changedAccount.getCurrency());
    }

    /**
     * Make an account PUT request
     *
     * @param number account number
     * @param account account
     * @return response
     */
    private ValidatableResponse requestAccountPut(String number, Account account) {
        return given()
                .body(account)
                .header(new Header("Content-Type", ContentType.JSON.toString()))
                .when()
                .put(number)
                .then();
    }

    /**
     * Make an existing account GET request
     *
     * @param number account number
     * @return account data
     */
    private Account requestGuaranteedAccountGet(String number) {
        return requestAccountGet(number)
                .statusCode(Status.OK.getStatusCode())
                .extract().as(Account.class);
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
