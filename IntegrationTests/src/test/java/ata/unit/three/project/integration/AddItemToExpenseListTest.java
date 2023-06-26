package ata.unit.three.project.integration;

import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static ata.unit.three.project.integration.Utils.getApiEndpint;
import static io.restassured.RestAssured.given;

public class AddItemToExpenseListTest {

    private final MockNeat mockNeat = MockNeat.threadLocal();

    @Test
    void  addExpenseItemTest() {
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
    }

    @Test
    void  addMultipleExpenseItemTest() throws InterruptedException {
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
                .post(url + "expenselists/expenseitems")
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
                .post(url + "expenselists/expenseitems")
            .then()
                .statusCode(400);
    }

    @Test
    void expenseItemAlreadyExistsTest() {
        String url = getApiEndpint();
        String email = mockNeat.emails().get();
        String title = "Trip to " + mockNeat.cities().capitals().get();

        String expenseListId =  Utils.createExpenseList(url, email, title);
        String amount = mockNeat.ints()
                .range(100, 1000)
                .valStr();

        String expenseId =  Utils.createExpense(url, email, title, amount);

        Map<String, String> expenseItemList = new HashMap<>();
        expenseItemList.put("expenseListId", expenseListId);
        expenseItemList.put("expenseItemId",expenseId);

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
                .post(url + "expenselists/expenseitems")
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
                .post(url + "expenselists/expenseitems")
            .then()
                .statusCode(400);
    }
}
