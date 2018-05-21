package com.megustav.revolut.test;

import com.megustav.revolut.data.Currency;
import com.megustav.revolut.data.OperationType;
import com.megustav.revolut.rest.data.AccountGetResponse;
import com.megustav.revolut.rest.data.AccountPayload;
import com.megustav.revolut.rest.data.OperationPayload;
import com.megustav.revolut.rest.data.OperationsGetResponse;
import com.megustav.revolut.rest.data.OperationsGetResponse.OperationInfo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response.Status;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Accounts operations handler tests
 *
 * TODO ideally there should also be some basic raw JSON-payload tests
 *
 * @author MeGustav
 * 05/05/2018 20:00
 */
public class AccountsOperationsHandlerIT {

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
     * Test getting getting operations of non existent account
     */
    @Test
    public void testNoAccountFound() {
        given()
                .pathParam("account", "42307810991000000001")
                .when()
                .get("{account}/operations")
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Test getting getting empty operations
     */
    @Test
    public void testNoOperationsFound() {
        String account = "42307810991000000002";
        insertAccount(account, BigDecimal.TEN, Currency.RUR);
        OperationsGetResponse response = getOperations("42307810991000000002");
        assertThat(response).as("Response").isNotNull();
        assertThat(response.getOperations()).as("Operations").isEmpty();
    }

    /**
     * Test operation invalid amount
     */
    @Test
    public void testOperationInvalidAmount() {
        OperationPayload payload = new OperationPayload(
                OperationType.WITHDRAWAL,
                BigDecimal.TEN.negate(),
                Currency.RUR
        );
        postOperationResponse("42307810991000000003", payload)
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Test operation invalid currency
     */
    @Test
    public void testOperationInvalidCurrency() {
        String account = "42307810991000000004";
        insertAccount(account, BigDecimal.TEN, Currency.RUR);
        OperationPayload payload = new OperationPayload(
                OperationType.WITHDRAWAL,
                BigDecimal.TEN,
                Currency.USD
        );
        postOperationResponse(account, payload)
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Test operation withdrawal insufficient funds
     */
    @Test
    public void testOperationInsufficientFunds() {
        String account = "42307810991000000005";
        insertAccount(account, BigDecimal.TEN, Currency.RUR);
        OperationPayload payload = new OperationPayload(
                OperationType.WITHDRAWAL,
                new BigDecimal("100.00"),
                Currency.RUR
        );
        postOperationResponse(account, payload)
                .statusCode(Status.CONFLICT.getStatusCode());
    }

    /**
     * Test that insufficient funds does not apply to deposit
     */
    @Test
    public void testInsufficientFundsDoesNotApplyToDeposit() {
        String account = "42307810991000000006";
        insertAccount(account, BigDecimal.TEN, Currency.RUR);
        OperationPayload payload = new OperationPayload(
                OperationType.DEPOSIT,
                new BigDecimal("100.00"),
                Currency.RUR
        );
        postOperationResponse(account, payload)
                .statusCode(Status.OK.getStatusCode());
    }

    /**
     * Test withdrawal
     */
    @Test
    public void testWithdrawalSuccess() {
        String number = "42307810991000000007";
        insertAccount(number, new BigDecimal("100.00"), Currency.RUR);
        OperationPayload payload = new OperationPayload(
                OperationType.WITHDRAWAL,
                new BigDecimal("34.00"),
                Currency.RUR
        );
        insertOperation(number, payload);

        // Checking account and operations
        AccountGetResponse account = getAccount(number);
        assertThat(account.getCurrentBalance()).as("Account balance")
                .isEqualByComparingTo(new BigDecimal("66.00"));

        OperationsGetResponse operationsResponse = getOperations(number);
        assertThat(operationsResponse).as("Response").isNotNull();

        List<OperationInfo> operations = operationsResponse.getOperations();
        assertThat(operations).as("Operation list").hasSize(1);

        OperationInfo info = operations.get(0);
        assertThat(info.getAccount()).as("Account number")
                .isEqualTo(number);
        assertThat(info.getAmount()).as("Operation amount")
                .isEqualByComparingTo(payload.getAmount());
        assertThat(info.getType()).as("Operation type").isEqualTo(OperationType.WITHDRAWAL);
        assertThat(info.getActionTime()).as("Action time")
                .isEqualToIgnoringMinutes(new Date());
    }

    /**
     * Test deposit
     */
    @Test
    public void testDepositSuccess() {
        String number = "42307810991000000008";
        insertAccount(number, new BigDecimal("200.00"), Currency.RUR);
        OperationPayload payload = new OperationPayload(
                OperationType.DEPOSIT,
                new BigDecimal("99.00"),
                Currency.RUR
        );
        insertOperation(number, payload);

        // Checking account and operations
        AccountGetResponse account = getAccount(number);
        assertThat(account.getCurrentBalance()).as("Account balance")
                .isEqualByComparingTo(new BigDecimal("299.00"));

        OperationsGetResponse operationsResponse = getOperations(number);
        assertThat(operationsResponse).as("Response").isNotNull();

        List<OperationInfo> operations = operationsResponse.getOperations();
        assertThat(operations).as("Operation list").hasSize(1);

        OperationInfo info = operations.get(0);
        assertThat(info.getAccount()).as("Account number")
                .isEqualTo(number);
        assertThat(info.getAmount()).as("Operation amount")
                .isEqualByComparingTo(payload.getAmount());
        assertThat(info.getType()).as("Operation type").isEqualTo(payload.getType());
        assertThat(info.getActionTime()).as("Action time")
                .isEqualToIgnoringMinutes(new Date());
    }

    /**
     * Test multiple operations
     */
    @Test
    public void testMultipleOperationsSuccess() {
        String number = "42307810991000000009";
        insertAccount(number, new BigDecimal("1000.00"), Currency.RUR);

        insertOperation(number, new OperationPayload(
                OperationType.DEPOSIT, new BigDecimal("200.00"), Currency.RUR));
        insertOperation(number, new OperationPayload(
                OperationType.WITHDRAWAL, new BigDecimal("50.00"), Currency.RUR));

        // Checking account and operations
        AccountGetResponse account = getAccount(number);
        assertThat(account.getCurrentBalance()).as("Account balance")
                .isEqualByComparingTo(new BigDecimal("1150.00"));
        OperationsGetResponse operationsResponse = getOperations(number);
        assertThat(operationsResponse).as("Response").isNotNull();
        List<OperationInfo> operations = operationsResponse.getOperations();
        assertThat(operations).as("Operation list").hasSize(2);
    }

    /**
     * Test that accounts don't interfere with each other
     */
    @Test
    public void testMultipleAccountsOperations() {
        String number1 = "42307810991000000010";
        String number2 = "42307810991000000011";
        insertAccount(number1, new BigDecimal("9000.00"), Currency.EUR);
        insertAccount(number2, new BigDecimal("15000.00"), Currency.EUR);

        insertOperation(number1, new OperationPayload(
                OperationType.DEPOSIT, new BigDecimal("100.00"), Currency.EUR));
        insertOperation(number1, new OperationPayload(
                OperationType.WITHDRAWAL, new BigDecimal("50.00"), Currency.EUR));
        insertOperation(number2, new OperationPayload(
                OperationType.WITHDRAWAL, new BigDecimal("10000.00"), Currency.EUR));

        // Checking accounts and operations
        AccountGetResponse account1 = getAccount(number1);
        assertThat(account1.getCurrentBalance()).as("Account1 balance")
                .isEqualByComparingTo(new BigDecimal("9050.00"));
        OperationsGetResponse operationsResponse1 = getOperations(number1);
        assertThat(operationsResponse1).as("Account1 response").isNotNull();
        List<OperationInfo> operations1 = operationsResponse1.getOperations();
        assertThat(operations1).as("Account1 operation list").hasSize(2);

        AccountGetResponse account2 = getAccount(number2);
        assertThat(account2.getCurrentBalance()).as("Account2 balance")
                .isEqualByComparingTo(new BigDecimal("5000.00"));
        OperationsGetResponse operationsResponse2 = getOperations(number2);
        assertThat(operationsResponse2).as("Account2 response").isNotNull();
        List<OperationInfo> operations2 = operationsResponse2.getOperations();
        assertThat(operations2).as("Account2 operation list").hasSize(1);
    }

    /**
     * Get operations
     *
     * @param account account number
     * @return reponse
     */
    private OperationsGetResponse getOperations(String account) {
        return given()
                .pathParam("account", account)
                .when()
                .get("{account}/operations")
                .then()
                .statusCode(Status.OK.getStatusCode()).extract().as(OperationsGetResponse.class);
    }

    /**
     * Insert operation
     *
     * @param account account number
     * @param payload operation
     */
    private void insertOperation(String account, OperationPayload payload) {
        postOperationResponse(account, payload)
                .statusCode(Status.OK.getStatusCode());
    }

    /**
     * Make a post request to insert an account
     *
     * @param account account number
     * @param payload operation
     * @return {@link ValidatableResponse} response
     */
    private ValidatableResponse postOperationResponse(String account, OperationPayload payload) {
        return given()
                .body(payload)
                .header(new Header("Content-Type", ContentType.JSON.toString()))
                .pathParam("account", account)
                .when()
                .post("{account}/operation")
                .then();
    }

    /**
     * Make an account POST request
     *
     * @param number account number
     * @param balance account initial balance
     * @param currency account currency
     */
    private void insertAccount(String number, BigDecimal balance, Currency currency) {

        given()
                .body(new AccountPayload(number, balance, currency))
                .header(new Header("Content-Type", ContentType.JSON.toString()))
                .when()
                .post()
                .then()
                .statusCode(Status.CREATED.getStatusCode());
    }

    /**
     * Make an existing account GET request
     *
     * @param number account number
     * @return account data
     */
    private AccountGetResponse getAccount(String number) {
        return given()
                .when()
                .get(number)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract().as(AccountGetResponse.class);
    }

}
