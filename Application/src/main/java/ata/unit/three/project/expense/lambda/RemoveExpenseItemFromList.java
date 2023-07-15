package ata.unit.three.project.expense.lambda;

import ata.unit.three.project.expense.dynamodb.ExpenseItem;
import ata.unit.three.project.expense.lambda.models.ExpenseItemList;
import ata.unit.three.project.expense.service.DaggerExpenseServiceComponent;
import ata.unit.three.project.expense.service.ExpenseService;
import ata.unit.three.project.expense.service.ExpenseServiceComponent;
import ata.unit.three.project.expense.service.exceptions.InvalidDataException;
import ata.unit.three.project.expense.service.exceptions.ItemNotFoundException;
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
public class RemoveExpenseItemFromList
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static final Logger log = LogManager.getLogger();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        // Logging the request json to make debugging easier.
        log.info(gson.toJson(input));

        ExpenseServiceComponent dagger = DaggerExpenseServiceComponent.create();
        ExpenseService expenseService = dagger.expenseService();

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        // Your Code Here...

        try {
            ExpenseItemList expenseItemList = gson.fromJson(input.getBody(), ExpenseItemList.class);

            String expenseListId = expenseItemList.getExpenseListId();
            String expenseId = expenseItemList.getExpenseItemId();

            expenseService.removeExpenseItemFromList(expenseListId, expenseId);

            response.setStatusCode(204);
        } catch (InvalidDataException | ItemNotFoundException e) {
            response.setStatusCode(400);
            response.setBody(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setBody("Internal Server Error");
            log.error("An error occurred while processing the request", e);
        }

        return response;
    }
}
