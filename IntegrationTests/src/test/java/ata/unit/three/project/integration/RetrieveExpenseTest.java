package ata.unit.three.project.integration;

import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Test;

import static ata.unit.three.project.integration.Utils.getApiEndpint;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RetrieveExpenseTest {

    private final MockNeat mockNeat = MockNeat.threadLocal();

    @Test
    void getExpenseByIdTest() {
        String url = getApiEndpint();
        String email = mockNeat.emails().get();
        String title = "Expense on gas for " + mockNeat.cars().get();
        String amount = mockNeat.ints()
                .range(100, 1000)
                .valStr();

        String expenseId = Utils.createExpense(url, email, title, amount);
        assertNotEquals("", expenseId, "The createExpense failed to produce an expense ID.");
        String expenseIdFromResponse =
            given()
            .when()
                .get(url + "expenses/" + expenseId)
            .then()
                .extract().response().body().path("id");

        assertEquals(expenseId, expenseIdFromResponse);
    }

    @Test
    void invalidExpenseIdTest() {
        String randomString = mockNeat.strings().val();
        given()
            .when()
                .get(getApiEndpint() + "expenses/" + randomString)
            .then()
                .statusCode(400);
    }

    @Test
    void expenseDoesNotExistTest() {
        String randomUUID = mockNeat.uuids().val();

        given()
            .when()
                .get(getApiEndpint() + "expenses/" + randomUUID)
            .then()
                .statusCode(404);
    }
}
