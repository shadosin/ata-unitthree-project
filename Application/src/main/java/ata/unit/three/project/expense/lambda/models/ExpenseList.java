package ata.unit.three.project.expense.lambda.models;

import com.kenzie.ata.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
public class ExpenseList {
    private String email;
    private String title;

    public ExpenseList(String email, String title) {
        this.email = email;
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
