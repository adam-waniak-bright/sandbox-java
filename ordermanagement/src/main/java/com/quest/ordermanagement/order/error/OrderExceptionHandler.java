package com.quest.ordermanagement.order.error;

import com.quest.ordermanagement.customer.error.CustomerNotFoundException;
import com.quest.ordermanagement.order.api.model.ErrorResponse;
import com.quest.ordermanagement.product.error.ProductNotFoundException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        var errorResponse = new ErrorResponse().error("customer_not_found").message(ex.getMessage());
        // TODO: set details and timestamp in all the error responses
        log.error("Customer not found: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex) {
        var errorResponse = new ErrorResponse().error("product_not_found").message(ex.getMessage());
        log.error("Product not found: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        var errorResponse = new ErrorResponse().error("order_not_found").message(ex.getMessage());
        log.error("Order not found: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        var errorResponse = new ErrorResponse().error("validation_error").message(errorDetails);
        log.error("Validation error: {}", errorDetails, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
