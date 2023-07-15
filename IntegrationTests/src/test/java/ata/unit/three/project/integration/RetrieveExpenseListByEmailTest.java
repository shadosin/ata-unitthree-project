package ata.unit.three.project.integration;

import ata.unit.three.project.expense.dynamodb.ExpenseItemList;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static ata.unit.three.project.integration.Utils.getApiEndpint;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RetrieveExpenseListByEmailTest {

    private final MockNeat mockNeat = MockNeat.threadLocal();

    @Test
    void getExpenseListByEmailTest() {
        String email = mockNeat.emails().get();
        String title = "Trip to " + mockNeat.cities().capitals().get();
        String url = getApiEndpint();

        String listId = Utils.createExpenseList(url,email,title);

        String idFromResponse =
            given()
            .when()
                .get(url + "expenselists?email=" + email)
            .then()
                .extract().response().body().path("[0].id");

        assertEquals(listId, idFromResponse);
    }

    @Test
    void noEmailPresentTest() {
        String email = "";
        given()
            .when()
                .get(getApiEndpint() + "expenselists?email=" + email)
            .then()
                .statusCode(400);
    }

    @Test
    void getExpenseListByEmailWithMultipleItemsTest() {
        //GIVEN
        String url = getApiEndpint();
        String email = mockNeat.emails().get();
        String title = "Trip to " + mockNeat.cities().capitals().get();

        String expenseListId = Utils.createExpenseList(url, email, title);
        String amount = mockNeat.ints()
                .range(100, 1000)
                .valStr();

        String expenseId = Utils.createExpense(url, email, title, amount);


        Map<String, String> expenseItemList = new HashMap<>();
        expenseItemList.put("expenseListId", expenseListId);
        expenseItemList.put("expenseItemId", expenseId);

        String secondExpenseId = Utils.createExpense(url, email, title, amount);
        given()
                .contentType("application/json")
                .body(expenseItemList)
            .when()
                .post(url + "expenselists/expenseitems")
            .then()
                .statusCode(204);

        Map<String, String> secondExpenseItemList = new HashMap<>();
        secondExpenseItemList.put("expenseListId", expenseListId);
        secondExpenseItemList.put("expenseItemId", secondExpenseId);

        given()
                .contentType("application/json")
                .body(secondExpenseItemList)
            .when()
                .post(url + "expenselists/expenseitems")
            .then()
                .statusCode(204);


        ExpenseItemList[] expenseItemLists =
                given()
                    .when()
                        .get(url + "expenselists?email=" + email)
                    .then()
                        .extract().response().body().as(ExpenseItemList[].class);

        assertEquals(expenseItemLists.length, 1);

        ExpenseItemList expenseList = expenseItemLists[0];
        // Descending order, so most recent expense first
        assertEquals(expenseList.getExpenseItems().get(0).getId(), secondExpenseId);

        assertEquals(expenseList.getExpenseItems().get(1).getId(), expenseId);
    }
}
