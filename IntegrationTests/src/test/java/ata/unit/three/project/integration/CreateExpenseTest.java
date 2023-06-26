package ata.unit.three.project.integration;

import io.restassured.response.ResponseOptions;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.*;

import static ata.unit.three.project.integration.Utils.getApiEndpint;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateExpenseTest {

    private final MockNeat mockNeat = MockNeat.threadLocal();

    @Test
    void createExpenseTest() {
        String email = mockNeat.emails().get();
        String amount = mockNeat.ints()
                .range(100, 1000)
                .valStr();

        Map<String, String> expense = new HashMap<>();
        expense.put("title", "Expense on gas for " + mockNeat.cars().get());
        expense.put("amount", amount);
        expense.put("email", email);

        String response =
                given()
                        .contentType("application/json")
                        .body(expense)
                        .when()
                        .post(getApiEndpint() + "expenses")
                        .then()
                        .statusCode(200)
                        .extract().body().asString();
        Assertions.assertDoesNotThrow(() -> {
            UUID.fromString(response);
        }, "The create expense response should contain a valid UUID");
    }

    @Test
    void createExpenseTestInvalidBody() {
        String email = mockNeat.emails().get();

        Map<String, String> expense = new HashMap<>();
        expense.put("title", "Expense on gas for " + mockNeat.cars().get());
        expense.put("amount", "wrong amount");
        expense.put("email", email);

        ResponseOptions response = given()
                .contentType("application/json")
                .body(expense)
                .post(getApiEndpint() + "expenses");

        int responseCode = response.statusCode();

        assertTrue(responseCode == 400 || responseCode == 502);
    }

}
