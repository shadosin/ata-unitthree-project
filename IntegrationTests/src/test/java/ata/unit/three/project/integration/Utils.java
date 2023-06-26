package ata.unit.three.project.integration;

import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.GetRestApisRequest;
import com.amazonaws.services.apigateway.model.GetRestApisResult;
import com.amazonaws.services.apigateway.model.RestApi;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class Utils {

    public static String apiEndpoint = null;

    public static String getApiEndpint() {
        if (apiEndpoint != null) {
            return apiEndpoint;
        }
        String region = System.getenv("AWS_REGION");
        if (region == null) {
            region = "us-east-1";
        }

        String deploymentName = System.getenv("UNIT_THREE_DEPLOY_STACK");
        if (deploymentName == null) {
            deploymentName = System.getenv("STACK_NAME");
        }
        if (deploymentName == null) {
            throw new IllegalArgumentException("Could not find the deployment name in environment variables.  Make sure that you have set up your environment variables using the setupEnvironment.sh script.");
        }

        AmazonApiGateway apiGatewayClient = AmazonApiGatewayClientBuilder.defaultClient();
        GetRestApisRequest request = new GetRestApisRequest();
        request.setLimit(500);
        GetRestApisResult result = apiGatewayClient.getRestApis(request);

        String endpointId = null;
        for (RestApi restApi : result.getItems()) {
            if (restApi.getName().equals(deploymentName)) {
                endpointId = restApi.getId();
                break;
            }
        }
        if (endpointId == null) {
            throw new IllegalArgumentException("Could not locate the API Gateway endpoint.  Make sure that your API is deployed and that your AWS credentials are valid.");
        }

        apiEndpoint = "https://" + endpointId + ".execute-api." + region + ".amazonaws.com/Prod/";

        return apiEndpoint;
    }

    public static String createExpense(String url, String email, String title, String amount) {
        Map<String, String> expense = new HashMap<>();
        expense.put("title", title);
        expense.put("amount", amount);
        expense.put("email", email);

        String expenseId =
            given()
                .contentType("application/json")
                .body(expense)
            .when()
                .post(url + "expenses")
            .then()
                .extract().body().asString();
        return expenseId;
    }


    public static String createExpenseList(String url, String email, String title) {
        Map<String, String> expense = new HashMap<>();
        expense.put("title", title);
        expense.put("email", email);

        String id =
            given()
                .contentType("application/json")
                .body(expense)
            .when()
                .post(url + "expenselists")
            .then()
                .extract().body().asString();

        return id;


    }
}
