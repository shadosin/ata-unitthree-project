package ata.unit.three.project.expense.service;

import ata.unit.three.project.expense.dynamodb.ExpenseItem;
import ata.unit.three.project.expense.dynamodb.ExpenseItemList;
import ata.unit.three.project.expense.dynamodb.ExpenseServiceRepository;
import ata.unit.three.project.expense.lambda.models.Expense;
import ata.unit.three.project.expense.lambda.models.ExpenseList;
import ata.unit.three.project.expense.service.exceptions.InvalidDataException;
import ata.unit.three.project.expense.service.exceptions.InvalidExpenseException;
import ata.unit.three.project.expense.service.exceptions.ItemNotFoundException;
import ata.unit.three.project.expense.service.model.ExpenseItemConverter;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Comparator;
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
        ExpenseItemList expenseItemList = expenseServiceRepository.getExpenseListById(id);
        ExpenseItem expenseItem = expenseServiceRepository.getExpenseById(expenseId);

            if (expenseItem == null) {
                throw new InvalidExpenseException("Expense doesn't exists");
            }
           if(expenseItemList == null){
               throw new InvalidDataException("Expense Item List does not exist");
           }
           // Check if the expense item's email matches the expense list's email
            if (!expenseItem.getEmail().equals(expenseItemList.getEmail())) {
                throw new InvalidDataException("Expense item cannot be added to this expense list");
            }

           if(expenseItemList.getExpenseItems() == null){
               expenseServiceRepository.addExpenseItemToList(id, expenseItem);

           }else if (expenseItemList.getExpenseItems().contains(expenseItem)) {
                throw new InvalidDataException("Expense item already exists in the expense list");

            }else{
               expenseServiceRepository.addExpenseItemToList(id, expenseItem);
            }
    }



    public void removeExpenseItemFromList(String id, String expenseId) {
        // Your Code Here
        ExpenseItemList expenseItemList = expenseServiceRepository.getExpenseListById(id);
        ExpenseItem expenseItem = expenseServiceRepository.getExpenseById(expenseId);

        if (expenseItem == null) {
            throw new InvalidExpenseException("Expense doesn't exists");
        }
        if(expenseItemList == null){
            throw new InvalidDataException("Expense Item List does not exist");
        }
        // Check if the expense item's email matches the expense list's email
        if (!expenseItem.getEmail().equals(expenseItemList.getEmail())) {
            throw new InvalidDataException("Expense item cannot be removed from this expense list");
        }

        if(expenseItemList.getExpenseItems() == null){
            throw new InvalidDataException("Cannot remove item from null list");

        }else if (expenseItemList.getExpenseItems().contains(expenseItem)) {

            expenseServiceRepository.removeExpenseItemToList(id, expenseItem);
        }

    }
//found date comparator on overstack https://stackoverflow.com/questions/5927109/sort-objects-in-arraylist-by-date
    public List<ExpenseItemList> getExpenseListByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            throw new InvalidDataException("Email is not present");
        }
       List < ExpenseItemList> bigExpenseList = expenseServiceRepository.getExpenseListsByEmail(email);
        for (ExpenseItemList expenseItemList : bigExpenseList) {
            List<ExpenseItem> expenseItems = expenseItemList.getExpenseItems();
            if(expenseItems == null){
                continue;
            }
            expenseItems.sort((o1, o2) -> o2.getExpenseDate().compareTo(o1.getExpenseDate()));

            expenseItemList.setExpenseItems(expenseItems);
        }

        return bigExpenseList;
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
