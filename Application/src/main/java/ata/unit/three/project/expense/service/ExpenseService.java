package ata.unit.three.project.expense.service;

import ata.unit.three.project.expense.dynamodb.ExpenseItem;
import ata.unit.three.project.expense.dynamodb.ExpenseItemList;
import ata.unit.three.project.expense.dynamodb.ExpenseServiceRepository;
import ata.unit.three.project.expense.lambda.models.Expense;
import ata.unit.three.project.expense.service.exceptions.InvalidDataException;
import ata.unit.three.project.expense.service.exceptions.ItemNotFoundException;
import ata.unit.three.project.expense.service.model.ExpenseItemConverter;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import javax.inject.Inject;

import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;

public class ExpenseService {

    private ExpenseServiceRepository expenseServiceRepository;
    private ExpenseItemConverter expenseItemConverter;

    @Inject
    public ExpenseService(ExpenseServiceRepository expenseServiceRepository,
                          ExpenseItemConverter expenseItemConverter) {
        this.expenseServiceRepository = expenseServiceRepository;
        this.expenseItemConverter = expenseItemConverter;
    }

    public ExpenseItem getExpenseById(String expenseId) {
        if (StringUtils.isEmpty(expenseId) || isInvalidUuid(expenseId)) {
            throw new InvalidDataException("Expense id is not present");
        }
        return expenseServiceRepository.getExpenseById(expenseId);
    }

    public List<ExpenseItem> getExpensesByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            throw new InvalidDataException("Email is not present");
        }
        return expenseServiceRepository.getExpensesByEmail(email);
    }

    public String createExpense(Expense expense) {
        ExpenseItem expenseItem = expenseItemConverter.convert(expense);
        expenseServiceRepository.createExpense(expenseItem);
        return expenseItem.getId();
    }

    public void updateExpense(String expenseId, Expense updateExpense) {
        if (StringUtils.isEmpty(expenseId) || isInvalidUuid(expenseId)) {
            throw new InvalidDataException("Expense id is not present");
        }
        ExpenseItem item = expenseServiceRepository.getExpenseById(expenseId);
        if (item == null) {
            throw new ItemNotFoundException("Expense does not exist");
        }
        expenseServiceRepository.updateExpense(expenseId,
                updateExpense.getTitle(),
                updateExpense.getAmount());
    }

    public void deleteExpense(String expenseId) {
        if (StringUtils.isEmpty(expenseId) || isInvalidUuid(expenseId)) {
            throw new InvalidDataException("Expense id is not present");
        }
        expenseServiceRepository.deleteExpense(expenseId);
    }

    public String createExpenseList(String email, String title) {
        String expenseListId = randomUUID().toString();
        expenseServiceRepository.createExpenseList(expenseListId, email, title);
        return expenseListId;
    }

    public void addExpenseItemToList(String id, String expenseId) {
        // Your Code Here
    }

    public void removeExpenseItemFromList(String id, String expenseId) {
        // Your Code Here
    }

    public List<ExpenseItemList> getExpenseListByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            throw new InvalidDataException("Email is not present");
        }
        return expenseServiceRepository.getExpenseListsByEmail(email);
    }

    private boolean isInvalidUuid(String uuid) {
        try {
            fromString(uuid);
        } catch (IllegalArgumentException exception) {
            return true;
        }
        return false;
    }
}
