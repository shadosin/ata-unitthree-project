package ata.unit.three.project.expense.service;

import ata.unit.three.project.App;
import ata.unit.three.project.expense.dynamodb.ExpenseItem;
import ata.unit.three.project.expense.dynamodb.ExpenseItemList;
import ata.unit.three.project.expense.dynamodb.ExpenseServiceRepository;
import ata.unit.three.project.expense.lambda.RetrieveExpensesByEmail;
import ata.unit.three.project.expense.lambda.models.Expense;
import ata.unit.three.project.expense.service.exceptions.InvalidDataException;
import ata.unit.three.project.expense.service.exceptions.ItemNotFoundException;
import ata.unit.three.project.expense.service.model.ExpenseItemConverter;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Instant;
import java.util.*;

import static com.google.common.base.CharMatcher.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

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
    void getExpenseListByEmailTest() {
        // GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        String email = mockNeat.emails().val();
        ExpenseItemList expenseList = new ExpenseItemList();
        expenseList.setId(UUID.randomUUID().toString());
        expenseList.setEmail(email);
        expenseList.setTitle("Expense List");

        List<ExpenseItemList> expenseListItems = Collections.singletonList(expenseList);

        // WHEN
        when(expenseServiceRepository.getExpenseListsByEmail(email)).thenReturn(expenseListItems);

        // THEN
        List<ExpenseItemList> returnedExpenseList = expenseService.getExpenseListByEmail(email);
        assertNotNull(returnedExpenseList);
        assertEquals(returnedExpenseList.size(), 1);
        assertEquals(returnedExpenseList.get(0).getEmail(), email);
    }

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
     *  expenseService.createExpense
     *  ------------------------------------------------------------------------ **/
    @Test
    void createExpense_createsExpense(){
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);


        Expense expense = new Expense("any@gmail.com", "expense", 0.0); // Create an example expense object


        ExpenseItem expenseItem = new ExpenseItem();
        when(expenseItemConverter.convert(expense)).thenReturn(expenseItem);

        // Act
        expenseService.createExpense(expense);

        // Assert
        verify(expenseServiceRepository).createExpense(expenseItem);
    }



    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.updateExpense
     *  ------------------------------------------------------------------------ **/
    @Test
    void updateExpense_validExpenseId_updatesExpense() {
        // Arrange
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        String expenseId = UUID.randomUUID().toString();
        ExpenseItem expenseToUpdate = new ExpenseItem();
        expenseToUpdate.setId(expenseId);


        Expense updateExpense = new Expense("any@gmail.com", "fakeExpense", 12.0);
        updateExpense.setTitle("Updated Title");
        updateExpense.setAmount(100.0);

        ExpenseItem existingExpenseItem = new ExpenseItem(); // Mock existing expense item
        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(existingExpenseItem);

        // Act
        expenseService.updateExpense(expenseId, updateExpense);

        // Assert
        verify(expenseServiceRepository).updateExpense(expenseId, updateExpense.getTitle(), updateExpense.getAmount());
        // Add more assertions as needed based on your specific requirements
    }

    @Test
    void updateExpense_invalidExpenseId_throwsInvalidDataException() {
        // Arrange
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        String expenseId = ""; // Invalid expense ID
        Expense updateExpense = new Expense("any@gmail.com", "fakeExpense", 12.0);

        // Act and Assert
        assertThrows(InvalidDataException.class, () -> expenseService.updateExpense(expenseId, updateExpense));
    }

    @Test
    void updateExpense_nonExistingExpenseId_throwsInvalidDataException() {
        // Arrange
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        String expenseId = "nonExistingExpenseId"; // Non-existing expense ID
        Expense updateExpense = new Expense("any@gmail.com", "fakeExpense", 12.0);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(null);

        // Act and Assert
        assertThrows(InvalidDataException.class, () -> expenseService.updateExpense(expenseId, updateExpense));
    }


    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.deleteExpense
     *  ------------------------------------------------------------------------ **/
    @Test
    void delete_expense() {
        // GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        String expenseId = UUID.randomUUID().toString();
        ExpenseItem expenseToDelete = new ExpenseItem();
        expenseToDelete.setId(expenseId);

        // WHEN
        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseToDelete);
        expenseService.deleteExpense(expenseId);

        // THEN
        verify(expenseServiceRepository).deleteExpense(expenseId);
    }

    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.addExpenseItemToList
     *  ------------------------------------------------------------------------ **/

    // Write additional tests here
    @Test
    void addExpenseItemToList_validInputs_addsExpenseItemToList() {
        // Arrange
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        String expenseListId = "validExpenseListId";
        String expenseItemId = "validExpenseItemId";

        ExpenseItemList expenseItemList = new ExpenseItemList();
        expenseItemList.setId(expenseListId);
        expenseItemList.setEmail("example@example.com");
        expenseItemList.setExpenseItems(new ArrayList<>());

        ExpenseItem expenseItem = new ExpenseItem();
        expenseItem.setId(expenseItemId);
        expenseItem.setEmail("example@example.com");

        when(expenseServiceRepository.getExpenseListById(expenseListId)).thenReturn(expenseItemList);
        when(expenseServiceRepository.getExpenseById(expenseItemId)).thenReturn(expenseItem);

        // Act
        expenseService.addExpenseItemToList(expenseListId, expenseItemId);

        // Assert
        verify(expenseServiceRepository).addExpenseItemToList(expenseListId, expenseItem);
        // Add more assertions as needed based on your specific requirements
    }



    @Test
    void addExpenseItemToList_invalidExpenseListId_throwsInvalidDataException() {
        // Arrange
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        String invalidExpenseListId = "invalidExpenseListId";
        String expenseItemId = "validExpenseItemId";

        when(expenseServiceRepository.getExpenseListById(invalidExpenseListId)).thenReturn(null);

        // Act and Assert
        assertThrows(InvalidDataException.class, () -> expenseService.addExpenseItemToList(invalidExpenseListId, expenseItemId));
    }

    @Test
    void addExpenseItemToList_expenseListMismatch_throwsInvalidDataException() {
        // Arrange
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        String expenseListId = "validExpenseListId";
        String expenseItemId = "validExpenseItemId";

        ExpenseItemList expenseItemList = new ExpenseItemList();
        expenseItemList.setId(expenseListId);
        expenseItemList.setEmail("example1@example.com"); // Different email from the expense item

        ExpenseItem expenseItem = new ExpenseItem();
        expenseItem.setId(expenseItemId);
        expenseItem.setEmail("example2@example.com");

        when(expenseServiceRepository.getExpenseListById(expenseListId)).thenReturn(expenseItemList);
        when(expenseServiceRepository.getExpenseById(expenseItemId)).thenReturn(expenseItem);

        // Act and Assert
        assertThrows(InvalidDataException.class, () -> expenseService.addExpenseItemToList(expenseListId, expenseItemId));
    }




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
    @Test
    void createExpenseList_createsExpenseList() {
        // Arrange
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);


    }


}