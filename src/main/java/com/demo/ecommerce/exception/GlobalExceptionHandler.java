package com.demo.ecommerce.exception;

import com.demo.ecommerce.dto.SimpleApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<SimpleApiResponse> handleItemNotFound(ItemNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(new SimpleApiResponse(ex.getMessage()));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<SimpleApiResponse> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(new SimpleApiResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(ItemAlreadyExistsException.class)
    public ResponseEntity<SimpleApiResponse> handleItemAlreadyExists(ItemAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409
                .body(new SimpleApiResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SimpleApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(new SimpleApiResponse(ex.getMessage()));
    }

    // Handle validation errors for @Valid on request body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SimpleApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(new SimpleApiResponse("Validation failed: " + errors));
    }

    // Handle validation errors for @PathVariable and @RequestParam constraints
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<SimpleApiResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(new SimpleApiResponse("Validation failed: " + errors));
    }

    // A fallback handler for any other unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<SimpleApiResponse> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(new SimpleApiResponse("An unexpected error occurred: " + ex.getMessage()));
    }
}