package ata.unit.three.project.integration;

import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Test;

import static ata.unit.three.project.integration.Utils.getApiEndpint;
import static io.restassured.RestAssured.given;

public class DeleteExpenseTest {

    private final MockNeat mockNeat = MockNeat.threadLocal();

    @Test
    void deleteExpenseByIdTest() {
        String url = getApiEndpint();
        String email = mockNeat.emails().get();
        String title = "Expense on gas for " + mockNeat.cars().get();
        String amount = mockNeat.ints()
                .range(100, 1000)
                .valStr();

        String expenseId = Utils.createExpense(url, email, title, amount);
        given()
            .when()
                .delete(url + "expenses/" + expenseId)
            .then()
                .statusCode(204);

        given()
            .when()
                .get(url + "expenses/" + expenseId)
            .then()
                .statusCode(404);

    }

    @Test
    void invalidExpenseIdTest() {
        String randomString = mockNeat.words().val();
        given()
            .when()
                .delete(getApiEndpint() + "expenses/" + randomString)
            .then()
                .statusCode(400);
    }
}
