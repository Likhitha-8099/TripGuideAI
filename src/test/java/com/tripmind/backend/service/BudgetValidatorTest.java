
package com.tripmind.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.tripmind.backend.dto.AiItineraryResponse;
import com.tripmind.backend.dto.BudgetBreakdown;

class BudgetValidatorTest {

    private final BudgetValidator validator = new BudgetValidator();

    // Helper method to create valid expenses
    private BudgetBreakdown createValidExpenses() {

        BudgetBreakdown expenses = new BudgetBreakdown();

        expenses.setAccommodation(3000.0);
        expenses.setFood(1500.0);
        expenses.setTransport(2200.0);
        expenses.setActivities(4200.0);
        expenses.setMiscellaneous(600.0);

        return expenses;
    }

    // 1. Reject expenses exceeding budget
    @Test
    void shouldRejectTripWhenExpensesExceedBudget() {

        AiItineraryResponse response = new AiItineraryResponse();
        response.setExpenses(createValidExpenses());

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateAndCalculate(response, 5000)
        );
    }

    // 2. Reject missing expense category
    @Test
    void shouldRejectTripWhenExpenseCategoryIsMissing() {

        BudgetBreakdown expenses = createValidExpenses();
        expenses.setMiscellaneous(null);

        AiItineraryResponse response = new AiItineraryResponse();
        response.setExpenses(expenses);

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateAndCalculate(response, 15000)
        );
    }

    // 3. Reject negative expenses
    @Test
    void shouldRejectTripWhenExpenseIsNegative() {

        BudgetBreakdown expenses = createValidExpenses();
        expenses.setTransport(-200.0);

        AiItineraryResponse response = new AiItineraryResponse();
        response.setExpenses(expenses);

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateAndCalculate(response, 15000)
        );
    }

    // 4. Accept valid expenses and calculate totals
    @Test
    void shouldCalculateBudgetWhenExpensesAreValid() {

        AiItineraryResponse response = new AiItineraryResponse();
        response.setExpenses(createValidExpenses());

        AiItineraryResponse result =
                validator.validateAndCalculate(response, 15000);

        assertEquals(11500.0, result.getEstimatedCost());
        assertEquals(3500.0, result.getRemainingBudget());
        assertEquals(15000.0, result.getBudget());
    }

    // 5. Reject null AI response
    @Test
    void shouldRejectNullAiResponse() {

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateAndCalculate(null, 15000)
        );
    }

    // 6. Reject null expense breakdown
    @Test
    void shouldRejectNullExpenseBreakdown() {

        AiItineraryResponse response = new AiItineraryResponse();

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateAndCalculate(response, 15000)
        );
    }

    // 7. Reject NaN and Infinity
    @Test
    void shouldRejectNonFiniteExpenseAmounts() {

        BudgetBreakdown expenses = createValidExpenses();
        expenses.setFood(Double.NaN);

        AiItineraryResponse response = new AiItineraryResponse();
        response.setExpenses(expenses);

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateAndCalculate(response, 15000)
        );

        expenses.setFood(Double.POSITIVE_INFINITY);

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateAndCalculate(response, 15000)
        );
    }

    // 8. Reject invalid user budget
    @Test
    void shouldRejectZeroOrNegativeBudget() {

        AiItineraryResponse response = new AiItineraryResponse();
        response.setExpenses(createValidExpenses());

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateAndCalculate(response, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateAndCalculate(response, -1000)
        );
    }
}
