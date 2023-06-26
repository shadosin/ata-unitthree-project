package ata.unit.three.project.integration;

import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static ata.unit.three.project.integration.Utils.getApiEndpint;
import static io.restassured.RestAssured.given;

public class CreateExpenseListTest {

    private final MockNeat mockNeat = MockNeat.threadLocal();

    @Test
    void createExpenseListTest() {
        String email = mockNeat.emails().get();
        String title = "Trip to " + mockNeat.cities().capitals().get();

        Map<String, String> expense = new HashMap<>();
        expense.put("title", title);
        expense.put("email", email);

        String response =
            given()
                .contentType("application/json")
                .body(expense)
            .when()
                .post(getApiEndpint() + "expenselists")
            .then()
                .statusCode(200)
                .extract().body().asString();

        Assertions.assertDoesNotThrow(() -> {
            UUID.fromString(response);
        }, "The create expense list response should contain a valid UUID");
    }
}
