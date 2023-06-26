package ata.unit.three.project.expense.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.kenzie.ata.ExcludeFromJacocoGeneratedReport;

import java.util.List;
import java.util.Objects;

@ExcludeFromJacocoGeneratedReport
@DynamoDBTable(tableName = "ExpenseList")
public class ExpenseItemList {
    private String id;
    private String title;
    private String email;
    private List<ExpenseItem> expenseItems;

    @DynamoDBHashKey(attributeName = "Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "Title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDBAttribute(attributeName = "Expenses")
    public List<ExpenseItem> getExpenseItems() {
        return expenseItems;
    }

    public void setExpenseItems(List<ExpenseItem> expenseItems) {
        this.expenseItems = expenseItems;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "EmailIndex", attributeName = "Email")
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExpenseItemList that = (ExpenseItemList) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
