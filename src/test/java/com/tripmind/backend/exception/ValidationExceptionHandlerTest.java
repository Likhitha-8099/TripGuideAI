
package com.tripmind.backend.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import com.tripmind.backend.dto.ApiResponse;

class ValidationExceptionHandlerTest {

    private final ValidationExceptionHandler handler =
            new ValidationExceptionHandler();

    // TEST 1: Budget validation failure - HTTP 422
    @Test
    void shouldReturn422WhenBudgetValidationFails() {

        IllegalArgumentException exception =
                new IllegalArgumentException(
                        "Generated itinerary exceeds your budget"
                );

        ResponseEntity<ApiResponse<Void>> response =
                handler.handleIllegalArgumentException(exception);

        assertEquals(
                HttpStatus.UNPROCESSABLE_ENTITY,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());
    }

    // TEST 2: Trip not found - HTTP 404
    @Test
    void shouldReturn404WhenTripIsNotFound() {

        ResponseStatusException exception =
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Trip not found"
                );

        ResponseEntity<ApiResponse<Void>> response =
                handler.handleResponseStatusException(exception);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());
    }

    // TEST 3: Unexpected exception - HTTP 500
    @Test
    void shouldReturn500WhenUnexpectedExceptionOccurs() {

        Exception exception =
                new RuntimeException("Database connection failed");

        ResponseEntity<Map<String, Object>> response =
                handler.handleGeneralException(exception);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                500,
                response.getBody().get("status")
        );

        assertEquals(
                "Something went wrong. Please try again later.",
                response.getBody().get("message")
        );
    }

    // TEST 4: Invalid request fields - HTTP 400
    @Test
    void shouldReturn400WhenRequestValidationFails() {

        // Arrange
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(
                        new Object(),
                        "tripRequest"
                );

        bindingResult.addError(
                new FieldError(
                        "tripRequest",
                        "destination",
                        "Destination is required"
                )
        );

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult())
                .thenReturn(bindingResult);

        // Act
        ResponseEntity<Map<String, Object>> response =
                handler.handleValidationErrors(exception);

        // Assert
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                400,
                response.getBody().get("status")
        );

        assertEquals(
                "Validation failed",
                response.getBody().get("message")
        );

        Map<?, ?> errors =
                (Map<?, ?>) response.getBody().get("errors");

        assertEquals(
                "Destination is required",
                errors.get("destination")
        );
    }
}
