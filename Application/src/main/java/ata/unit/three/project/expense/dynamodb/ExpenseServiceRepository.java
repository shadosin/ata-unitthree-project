package ata.unit.three.project.expense.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.kenzie.ata.ExcludeFromJacocoGeneratedReport;
import javax.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import static ata.unit.three.project.expense.dynamodb.ExpenseTable.EXPENSE_LIST_TABLE_NAME;
import static ata.unit.three.project.expense.dynamodb.ExpenseTable.EXPENSE_TABLE_NAME;

// NOTE: You do not need to change anything in this file to complete the project.
@ExcludeFromJacocoGeneratedReport
public class ExpenseServiceRepository {

    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    DynamoDBMapper mapper = new DynamoDBMapper(client);

    public ExpenseServiceRepository() {
        if (!ExpenseTable.doesExpenseTableExist(EXPENSE_TABLE_NAME)) {
            ExpenseTable.createExpenseTable();
        }

        if (!ExpenseTable.doesExpenseTableExist(EXPENSE_LIST_TABLE_NAME)) {
            ExpenseTable.createExpenseListTable();
        }
    }

    public ExpenseItem getExpenseById(String expenseId) {
        ExpenseItem item = mapper.load(ExpenseItem.class, expenseId);
        return item;
    }

    public List<ExpenseItem> getExpensesByEmail(String email) {
        ExpenseItem expenseItem = new ExpenseItem();
        expenseItem.setEmail(email);

        DynamoDBQueryExpression<ExpenseItem> queryExpression = new DynamoDBQueryExpression<ExpenseItem>()
                .withIndexName("EmailIndex").withConsistentRead(true)
                .withHashKeyValues(expenseItem)
                .withConsistentRead(false);

        return mapper.query(ExpenseItem.class, queryExpression);
    }

    public void createExpense(ExpenseItem expense) {
        mapper.save(expense);
    }

    public void updateExpense(String expenseId, String title, Double amount) {
        ExpenseItem item = mapper.load(ExpenseItem.class, expenseId);
        item.setAmount(amount);
        item.setTitle(title);
        mapper.save(item);
    }

    public void deleteExpense(String expenseId) {
        ExpenseItem item = mapper.load(ExpenseItem.class, expenseId);
        mapper.delete(item);
    }

    public void createExpenseList(String expenseListId, String email, String title) {
        ExpenseItemList expenseItemList = new ExpenseItemList();
        expenseItemList.setId(expenseListId);
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(title);
        mapper.save(expenseItemList);
    }

    public void addExpenseItemToList(String id, ExpenseItem item) {
        ExpenseItemList list = mapper.load(ExpenseItemList.class, id);
        if (list.getExpenseItems() == null) {
            list.setExpenseItems(new LinkedList<>());
        }
        list.getExpenseItems().add(item);
        mapper.save(list);
    }

    public void removeExpenseItemToList(String id, ExpenseItem item) {
        ExpenseItemList list = mapper.load(ExpenseItemList.class, id);
        if (list.getExpenseItems() != null) {
            list.getExpenseItems().remove(item);
            mapper.save(list);
        }
    }

    public List<ExpenseItemList> getExpenseListsByEmail(String email) {
        ExpenseItemList expenseItemList = new ExpenseItemList();
        expenseItemList.setEmail(email);

        DynamoDBQueryExpression<ExpenseItemList> queryExpression = new DynamoDBQueryExpression<ExpenseItemList>()
                .withIndexName("EmailIndex").withConsistentRead(true)
                .withHashKeyValues(expenseItemList)
                .withConsistentRead(false);

        return mapper.query(ExpenseItemList.class, queryExpression);
    }

    public ExpenseItemList getExpenseListById(String id) {
        ExpenseItemList item = mapper.load(ExpenseItemList.class, id);
        return item;
    }

    public void deleteExpenseItemList(String expenseListId) {
        ExpenseItemList item = mapper.load(ExpenseItemList.class, expenseListId);
        mapper.delete(item);
    }
}
