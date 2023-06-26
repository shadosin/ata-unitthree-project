package ata.unit.three.project.expense.lambda.models;

public class ExpenseItemList {
    private String expenseItemId;
    private String expenseListId;

    public ExpenseItemList(String expenseItemId, String expenseListId) {
        this.expenseItemId = expenseItemId;
        this.expenseListId = expenseListId;
    }

    public String getExpenseItemId() {
        return expenseItemId;
    }

    public void setExpenseItemId(String expenseItemId) {
        this.expenseItemId = expenseItemId;
    }

    public String getExpenseListId() {
        return expenseListId;
    }

    public void setExpenseListId(String expenseListId) {
        this.expenseListId = expenseListId;
    }
}
