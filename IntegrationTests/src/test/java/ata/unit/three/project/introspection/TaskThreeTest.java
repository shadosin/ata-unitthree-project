package ata.unit.three.project.introspection;

import ata.unit.three.project.App;
import ata.unit.three.project.expense.dynamodb.ExpenseServiceRepository;
import ata.unit.three.project.expense.service.ExpenseService;
import ata.unit.three.project.expense.service.model.ExpenseItemConverter;
import com.kenzie.test.infrastructure.reflect.ClassQuery;
import com.kenzie.test.infrastructure.reflect.MethodInvoker;
import com.kenzie.test.infrastructure.reflect.MethodQuery;
import com.kenzie.test.infrastructure.reflect.NoMethodFoundException;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class TaskThreeTest {
    private static final String BASE_PACKAGE = "ata.unit.three.project.expense.service";

    @Test
    public void app_has_module_annotation() {
        // GIVEN
        Class<App> app = App.class;
        // WHEN / THEN
        assertTrue(checkForAnnotation(app.getAnnotations(), Module.class), "The App must have a Module Annotation");
    }

    @Test
    public void app_has_expense_service_repository() {
        // GIVEN
        Method expenseServiceMethod = assertDoesNotThrow(() -> MethodQuery.inType(App.class)
                .withReturnType(ExpenseServiceRepository.class)
                .findMethodOrFail(), "The Method returning an ExpenseServiceRepository must exist");
        // WHEN / THEN
        assertTrue(checkForAnnotation(expenseServiceMethod.getAnnotations(), Provides.class), "The ExpenseServiceRepository provider must have the Provides annotation");
    }

    @Test
    public void app_does_not_have_expense_service() {
        assertThrows(NoMethodFoundException.class, () -> MethodQuery.inType(App.class)
                .withReturnType(ExpenseService.class)
                .findMethod(), "The expenseService should be removed from App");

    }

    @Test
    public void app_has_expense_item_converter() {
        // GIVEN
        Method expenseItemConverterMethod = assertDoesNotThrow(() -> MethodQuery.inType(App.class)
                .withReturnType(ExpenseItemConverter.class)
                .findMethodOrFail(), "The Method returning an ExpenseItemConverter must exist");
        // WHEN / THEN
        assertTrue(checkForAnnotation(expenseItemConverterMethod.getAnnotations(), Provides.class), "The ExpenseItemConverter provider must have the Provides annotation");
    }

    @Test
    public void expense_service_component_exists() {
        // GIVEN
        Class<?> componentClass = assertDoesNotThrow( () -> ClassQuery.inContainingPackage(BASE_PACKAGE)
                .withExactSimpleName("ExpenseServiceComponent")
                .findClassOrFail(), "The ExpenseServiceComponent class must exist");
        // WHEN / THEN
        assertTrue(checkForAnnotation(componentClass.getAnnotations(), Component.class), "The ExpenseServiceComponent must have the Component annotation");
        assertDoesNotThrow(() -> componentClass.getMethod("expenseService"), "The expenseService field must exist on the ExpenseServiceComponent");
    }

    @Test
    public void check_app_for_expense_service_component() {
        // GIVEN - The ExpenseServiceComponent class and the generated Dagger counterpart
        Class<?> componentClass = assertDoesNotThrow( () -> ClassQuery.inContainingPackage(BASE_PACKAGE)
                .withExactSimpleName("ExpenseServiceComponent")
                .findClassOrFail(), "The ExpenseServiceComponent class must exist");

        Class<?> daggerClass = assertDoesNotThrow( () -> ClassQuery.inContainingPackage(BASE_PACKAGE)
                .withExactSimpleName("DaggerExpenseServiceComponent")
                .findClassOrFail(), "The DaggerExpenseServiceComponent generated class must exist");

        // WHEN - dagger is used to instantiate the expense service
        // ExpenseServiceComponent expenseServiceComponent = DaggerExpenseServiceComponent.create();
        // ExpenseService expenseService = expenseServiceComponent.expenseService();
        Method expenseServiceMethod = assertDoesNotThrow( () -> MethodQuery.inType(componentClass)
                .withExactName("expenseService")
                .findMethodOrFail(), "The ExpenseServiceComponent must have an expenseService property");
        Method createMethod = MethodQuery.inType(daggerClass)
                .withExactName("create")
                .findMethodOrFail();
        Object expenseServiceComponent = MethodInvoker.invokeStaticMethodWithReturnValue(createMethod);
        Object expenseService = MethodInvoker.invokeInstanceMethodWithReturnValue(expenseServiceComponent, expenseServiceMethod);

        // THEN - We get a valid expense service instance
        assertTrue(expenseService instanceof ExpenseService);
    }

    private static boolean checkForAnnotation(Annotation[] annotations, Class annotation) {

        for (Annotation currAnnotation : annotations) {
            if (currAnnotation.annotationType().equals(annotation)) {
                return true;
            }
        }
        return false;
    }
}
