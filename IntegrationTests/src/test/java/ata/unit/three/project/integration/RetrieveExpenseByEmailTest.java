package ata.unit.three.project.integration;

import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Test;

import static ata.unit.three.project.integration.Utils.getApiEndpint;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RetrieveExpenseByEmailTest {

    private final MockNeat mockNeat = MockNeat.threadLocal();

    @Test
    void getExpenseByEmailTest() {
        String url = getApiEndpint();
        String email = mockNeat.emails().get();
        String title = "Expense on gas for " + mockNeat.cars().get();
        String amount = mockNeat.ints()
                .range(100, 1000)
                .valStr();

        Utils.createExpense(url, email, title, amount);

        String emailFromResponse =
            given()
            .when()
                .get(url + "expenses?email=" + email)
            .then()
                .extract().response().body().path("[0].email");

        assertEquals(email, emailFromResponse);
    }

    @Test
    void noEmailPresentTest() {
        String email = "";
        given()
            .when()
                .get(getApiEndpint() + "expenses?email=" + email)
            .then()
                .statusCode(400);
    }
}
