package ata.unit.three.project.introspection;

import ata.unit.three.project.expense.dynamodb.ExpenseItem;
import ata.unit.three.project.expense.dynamodb.ExpenseItemList;
import ata.unit.three.project.expense.dynamodb.ExpenseServiceRepository;
import ata.unit.three.project.expense.service.ExpenseService;
import ata.unit.three.project.expense.service.model.ExpenseItemConverter;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskFiveTest {

    private final MockNeat mockNeat = MockNeat.threadLocal();
    
    @Test
    void get_expense_list_by_email_multiple_expense_items() {
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

        ExpenseItem secondExpenseItem = new ExpenseItem();
        String secondId = UUID.randomUUID().toString();
        String secondEmail = mockNeat.emails().val();
        secondExpenseItem.setId(secondId);
        secondExpenseItem.setEmail(secondEmail);
        secondExpenseItem.setExpenseDate(Instant.now().minusSeconds(1000).toString());
        secondExpenseItem.setTitle(mockNeat.strings().val());

        List<ExpenseItem> expenseItems = new ArrayList<>();
        expenseItems.add(secondExpenseItem);
        expenseItems.add(expenseItem);

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
        expenseItemList.setExpenseItems(expenseItems);
        List<ExpenseItemList> expenseItemLists = Collections.singletonList(expenseItemList);

        //WHEN
        when(expenseServiceRepository.getExpenseListsByEmail(email)).thenReturn(expenseItemLists);

        //THEN
        List<ExpenseItemList> returnedExpenseList = expenseService.getExpenseListByEmail(email);
        assertEquals(returnedExpenseList.size(), 1);
        assertEquals(returnedExpenseList.get(0).getExpenseItems().get(0).getId(), id);
        assertEquals(returnedExpenseList.get(0).getExpenseItems().get(0).getEmail(), email);

        assertEquals(returnedExpenseList.get(0).getExpenseItems().get(1).getId(), secondId);
        assertEquals(returnedExpenseList.get(0).getExpenseItems().get(1).getEmail(), secondEmail);
    }
}
