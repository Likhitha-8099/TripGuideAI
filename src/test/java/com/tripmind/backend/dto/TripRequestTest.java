
package com.tripmind.backend.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class TripRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    // TEST 1: Valid request should be accepted
    @Test
    void shouldAcceptValidTripRequest() {

        TripRequest request = new TripRequest();

        request.setDestination("Hyderabad");
        request.setNumberOfDays(3);
        request.setBudget(15000);
        request.setTravelStyle("Adventure");

        Set<ConstraintViolation<TripRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    // TEST 2: Empty fields should be rejected
    @Test
    void shouldRejectEmptyTripRequestFields() {

        TripRequest request = new TripRequest();

        request.setDestination("");
        request.setNumberOfDays(0);
        request.setBudget(0);
        request.setTravelStyle("");

        Set<ConstraintViolation<TripRequest>> violations =
                validator.validate(request);

        assertEquals(4, violations.size());
    }

    // TEST 3: Negative days and budget should be rejected
    @Test
    void shouldRejectNegativeDaysAndBudget() {

        TripRequest request = new TripRequest();

        request.setDestination("Hyderabad");
        request.setNumberOfDays(-2);
        request.setBudget(-5000);
        request.setTravelStyle("Adventure");

        Set<ConstraintViolation<TripRequest>> violations =
                validator.validate(request);

        assertEquals(2, violations.size());
    }

    // TEST 4: Whitespace-only strings should be rejected
    @Test
    void shouldRejectWhitespaceOnlyFields() {

        TripRequest request = new TripRequest();

        request.setDestination("   ");
        request.setNumberOfDays(3);
        request.setBudget(15000);
        request.setTravelStyle("   ");

        Set<ConstraintViolation<TripRequest>> violations =
                validator.validate(request);

        assertEquals(2, violations.size());
    }
}
