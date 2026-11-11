package com.tripmind.backend.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.tripmind.backend.dto.ApiResponse;

@RestControllerAdvice
public class ValidationExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException exceptiom) {
		Map<String, String> errors = new HashMap<>();

		exceptiom.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		Map<String, Object> response = new HashMap<>();

		response.put("status", 400);
		response.put("message", "Validation failed");
		response.put("errors", errors);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGeneralException(Exception exception) {

		Map<String, Object> response = new HashMap<>();

		response.put("status", 500);
		response.put("message", "Something went wrong. Please try again later.");

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException exception) {

		ApiResponse<Void> response = new ApiResponse<>(false, exception.getReason(), null);

		return new ResponseEntity<>(response, exception.getStatusCode());
	}
	

@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
        IllegalArgumentException exception) {

    ApiResponse<Void> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
    );

    return new ResponseEntity<>(
            response,
            HttpStatus.UNPROCESSABLE_ENTITY
    );
}

	

}
