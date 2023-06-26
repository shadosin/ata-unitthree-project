package ata.unit.three.project.integration;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ata.unit.three.project.integration.Utils.getApiEndpint;
import static io.restassured.RestAssured.given;

public class RemoveItemFromExpenseListTest {

    private final MockNeat mockNeat = MockNeat.threadLocal();

    @Test
    void  removeExpenseItemTest() {
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

        given()
                .contentType("application/json")
                .body(expenseItemList)
            .when()
                .post(url + "expenselists/expenseitems")
            .then()
                .statusCode(204);

        given()
                .contentType("application/json")
                .body(expenseItemList)
            .when()
                .delete(url + "expenselists/expenseitems")
            .then()
                .statusCode(204);

        Response response =
            given()
            .when()
                .get(url + "expenselists?email=" + email)
            .then()
                .extract().response();

        JsonPath js = new JsonPath(response.asString());
        List list = (List)js.get("[0].expenseItems");
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void expenseListIdDoesNotExistTest() {
        String url = getApiEndpint();
        String email = mockNeat.emails().get();
        String title = "Trip to " + mockNeat.cities().capitals().get();
        String amount = mockNeat.ints()
                .range(100, 1000)
                .valStr();

       String expenseId =  Utils.createExpense(url, email, title, amount);

        Map<String, String> expenseItemList = new HashMap<>();
        expenseItemList.put("expenseListId", UUID.randomUUID().toString());
        expenseItemList.put("expenseItemId", expenseId);

        given()
                .contentType("application/json")
                .body(expenseItemList)
            .when()
                .delete(url + "expenselists/expenseitems")
            .then()
                .statusCode(400);
    }


    @Test
    void expenseItemDoesNotExistTest() {
        String url = getApiEndpint();
        String email = mockNeat.emails().get();
        String title = "Trip to " + mockNeat.cities().capitals().get();

        String expenseListId =  Utils.createExpenseList(url, email, title);

        Map<String, String> expenseItemList = new HashMap<>();
        expenseItemList.put("expenseListId", expenseListId);
        expenseItemList.put("expenseItemId", UUID.randomUUID().toString());

        given()
                .contentType("application/json")
                .body(expenseItemList)
            .when()
                .delete(url + "expenselists/expenseitems")
            .then()
                .statusCode(400);
    }

    @Test
    void expenseItemAndListIdEmailDoesNotMatchTest() {
        String url = getApiEndpint();
        String email = mockNeat.emails().get();
        String anotherEmail = mockNeat.emails().get();
        String title = "Trip to " + mockNeat.cities().capitals().get();

        String expenseListId =  Utils.createExpenseList(url, email, title);
        String amount = mockNeat.ints()
                .range(100, 1000)
                .valStr();

        String expenseId =  Utils.createExpense(url, anotherEmail, title, amount);

        Map<String, String> expenseItemList = new HashMap<>();
        expenseItemList.put("expenseListId", expenseListId);
        expenseItemList.put("expenseItemId",expenseId);

        given()
                .contentType("application/json")
                .body(expenseItemList)
            .when()
                .delete(url + "expenselists/expenseitems")
            .then()
                .statusCode(400);
    }
}
