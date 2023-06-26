package ata.unit.three.project.expense.lambda;

import ata.unit.three.project.App;
import ata.unit.three.project.expense.lambda.models.ExpenseList;
import ata.unit.three.project.expense.service.ExpenseService;
import ata.unit.three.project.expense.service.ExpenseServiceComponent;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kenzie.ata.ExcludeFromJacocoGeneratedReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ExcludeFromJacocoGeneratedReport
public class CreateExpenseList implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static final Logger log = LogManager.getLogger();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        // Logging the request json to make debugging easier.
        log.info(gson.toJson(input));

        ExpenseService expenseService = App.expenseService();

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        ExpenseList expenseList = gson.fromJson(input.getBody(), ExpenseList.class);
        String id = expenseService.createExpenseList(expenseList.getEmail(), expenseList.getTitle());

        return response
                .withStatusCode(200)
                .withBody(id);
    }
}
