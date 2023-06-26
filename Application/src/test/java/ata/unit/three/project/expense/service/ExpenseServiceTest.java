package ata.unit.three.project.expense.service;

import ata.unit.three.project.expense.dynamodb.ExpenseItem;
import ata.unit.three.project.expense.dynamodb.ExpenseItemList;
import ata.unit.three.project.expense.dynamodb.ExpenseServiceRepository;
import ata.unit.three.project.expense.service.exceptions.InvalidDataException;
import ata.unit.three.project.expense.service.exceptions.ItemNotFoundException;
import ata.unit.three.project.expense.service.model.ExpenseItemConverter;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExpenseServiceTest {

    private final MockNeat mockNeat = MockNeat.threadLocal();

    /** ------------------------------------------------------------------------
     *  expenseService.getExpenseById
     *  ------------------------------------------------------------------------ **/

    @Test
    void get_expense_by_id() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        expenseItem.setId(id);
        expenseItem.setEmail(mockNeat.emails().val());
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());

        //WHEN
        when(expenseServiceRepository.getExpenseById(id)).thenReturn(expenseItem);

        //THEN
        ExpenseItem returnedExpenseItem = expenseService.getExpenseById(id);
        Assertions.assertEquals(returnedExpenseItem.getId(), expenseItem.getId());
        Assertions.assertEquals(returnedExpenseItem.getEmail(), expenseItem.getEmail());
        Assertions.assertEquals(returnedExpenseItem.getTitle(), expenseItem.getTitle());
        Assertions.assertEquals(returnedExpenseItem.getExpenseDate(), expenseItem.getExpenseDate());
    }

    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.getExpensesByEmail
     *  ------------------------------------------------------------------------ **/

    @Test
    void get_expenses_by_email() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());

        List<ExpenseItem> expenseItemList = Collections.singletonList(expenseItem);

        //WHEN
        when(expenseServiceRepository.getExpensesByEmail(email)).thenReturn(expenseItemList);

        //THEN
        List<ExpenseItem> returnedExpenseList = expenseService.getExpensesByEmail(email);
        assertEquals(returnedExpenseList.size(), 1);
        assertEquals(returnedExpenseList.get(0).getId(), id);
        assertEquals(returnedExpenseList.get(0).getEmail(), email);
    }

    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.updateExpense
     *  ------------------------------------------------------------------------ **/

    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.deleteExpense
     *  ------------------------------------------------------------------------ **/

    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.addExpenseItemToList
     *  ------------------------------------------------------------------------ **/

    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.removeExpenseItemFromList
     *  ------------------------------------------------------------------------ **/

    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.getExpenseListByEmail
     *  ------------------------------------------------------------------------ **/

    @Test
    void get_expense_list_by_email() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseListsByEmail(anyString())).thenReturn(list);

        //THEN
        List<ExpenseItemList> returnedExpenseList = expenseService.getExpenseListByEmail(email);
        assertEquals(returnedExpenseList.size(), 1);
        assertEquals(returnedExpenseList.get(0).getId(), expenseListId);
        assertEquals(returnedExpenseList.get(0).getEmail(), email);
    }

    // Write additional tests here

}