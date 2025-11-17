package com.demo.ecommerce.exception;

import com.demo.ecommerce.dto.SimpleApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

    // A fallback handler for any other unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<SimpleApiResponse> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(new SimpleApiResponse("An unexpected error occurred: " + ex.getMessage()));
    }
}