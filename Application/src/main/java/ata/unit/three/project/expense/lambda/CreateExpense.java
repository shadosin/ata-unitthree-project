package ata.unit.three.project.expense.lambda;

import ata.unit.three.project.App;
import ata.unit.three.project.expense.lambda.models.Expense;
import ata.unit.three.project.expense.service.DaggerExpenseServiceComponent;
import ata.unit.three.project.expense.service.ExpenseService;
import ata.unit.three.project.expense.service.ExpenseServiceComponent;

import ata.unit.three.project.expense.service.exceptions.InvalidExpenseException;
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
public class CreateExpense implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static final Logger log = LogManager.getLogger();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        Gson gson = new Gson();
        log.info(gson.toJson(input));


        ExpenseServiceComponent dagger = DaggerExpenseServiceComponent.create();
        ExpenseService expenseService = dagger.expenseService();


        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            Expense expense = gson.fromJson(input.getBody(), Expense.class);

            // Check if the amount is a valid positive integer
            double amount = expense.getAmount();
            if (amount <= 0) {
                throw new InvalidExpenseException("Invalid amount. Amount must be a positive integer.");
            }

            String id = expenseService.createExpense(expense);

            return response
                    .withStatusCode(200)
                    .withBody(id);
        } catch (InvalidExpenseException e) {
            return response
                    .withStatusCode(400)
                    .withBody("Invalid expense data: " + e.getMessage());
        }
    }
}
