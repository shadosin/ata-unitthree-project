package ata.unit.three.project.integration;

import io.restassured.response.ResponseOptions;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static ata.unit.three.project.integration.Utils.getApiEndpint;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateExpenseTest {

    private final MockNeat mockNeat = MockNeat.threadLocal();

    @Test
    void updateExpenseTest() {
        String url = getApiEndpint();
        String email = mockNeat.emails().get();
        String amount = mockNeat.ints()
                .range(100, 1000)
                .valStr();
        String title = "Expense on gas for " + mockNeat.cars().get();

        String expenseId = Utils.createExpense(url, email, title, amount);

        String newAmount = mockNeat.ints()
                .range(100, 1000)
                .valStr();

        Map<String, String> expense = new HashMap<>();
        expense.put("title", title);
        expense.put("amount", newAmount);
        expense.put("email", email);

        given()
                .contentType("application/json")
                .body(expense)
            .when()
                .put(url + "expenses/" + expenseId)
            .then()
                .statusCode(204);

        Float amountFromResponse =
            given()
                .contentType("application/json")
            .when()
                .get(url + "expenses/" + expenseId)
            .then()
                .extract().response().body().path("amount");

        assertEquals(Double.parseDouble(newAmount), amountFromResponse.doubleValue());

    }

    @Test
    void updateExpenseTestExpenseItemDoesNotExist() {
        String randomUUID = mockNeat.uuids().val();
        String email = mockNeat.emails().get();
        String title = "Expense on gas for " + mockNeat.cars().get();
        String amount = mockNeat.ints()
                .range(100, 1000)
                .valStr();


        Map<String, String> expense = new HashMap<>();
        expense.put("title", title);
        expense.put("amount", amount);
        expense.put("email", email);

        given()
                .contentType("application/json")
                .body(expense)
            .when()
                .put(getApiEndpint() + "expenses/" + randomUUID)
            .then()
                .statusCode(404);
    }

    @Test
    void updateExpenseInvalidBody() {
        String url = getApiEndpint();
        String randomUUID = mockNeat.uuids().val();
        String email = mockNeat.emails().get();
        String title = "Expense on gas for " + mockNeat.cars().get();
        String amount = mockNeat.ints()
                .range(100, 1000)
                .valStr();


        Map<String, String> expense = new HashMap<>();
        expense.put("title", title);
        expense.put("amount", "wrong amount");
        expense.put("email", email);

        String expenseId = Utils.createExpense(url, email, title, amount);

        ResponseOptions response = given()
                .contentType("application/json")
                .body(expense)
                .put(url + "expenses/" + expenseId);

        int responseCode = response.statusCode();

        assertTrue(responseCode == 400 || responseCode == 502);
    }
}
