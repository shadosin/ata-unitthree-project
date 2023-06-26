package ata.unit.three.project;

import ata.unit.three.project.expense.dynamodb.ExpenseItem;
import ata.unit.three.project.expense.dynamodb.ExpenseServiceRepository;
import ata.unit.three.project.expense.service.ExpenseService;
import ata.unit.three.project.expense.service.model.ExpenseItemConverter;
import dagger.Module;
import dagger.Provides;

public class App {
    public static ExpenseService expenseService() {
        return new ExpenseService(new ExpenseServiceRepository(), new ExpenseItemConverter());
    }
}
