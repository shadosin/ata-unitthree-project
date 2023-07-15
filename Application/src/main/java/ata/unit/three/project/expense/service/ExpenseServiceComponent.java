package ata.unit.three.project.expense.service;

import ata.unit.three.project.App;
import ata.unit.three.project.expense.service.model.ExpenseItemConverter;
import dagger.Component;

@Component(modules = {App.class})
public interface ExpenseServiceComponent {
    ExpenseService expenseService();
    ExpenseItemConverter expenseItemConverter();
}
