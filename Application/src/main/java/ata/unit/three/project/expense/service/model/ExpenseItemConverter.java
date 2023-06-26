package ata.unit.three.project.expense.service.model;

import ata.unit.three.project.expense.dynamodb.ExpenseItem;
import ata.unit.three.project.expense.lambda.models.Expense;

import javax.inject.Inject;
import java.time.Instant;
import java.util.UUID;


public class ExpenseItemConverter {
    
    public ExpenseItemConverter() {

    }

    public  ExpenseItem convert(Expense expense) {
        ExpenseItem expenseItem = new ExpenseItem();
        expenseItem.setId(UUID.randomUUID().toString());
        expenseItem.setEmail(expense.getEmail());
        expenseItem.setTitle(expense.getTitle());
        expenseItem.setAmount(expense.getAmount());
        expenseItem.setExpenseDate(Instant.now().toString());
        return expenseItem;
    }
}
