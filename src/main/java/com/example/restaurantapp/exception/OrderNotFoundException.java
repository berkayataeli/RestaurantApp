package com.example.restaurantapp.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("Order Not Found for orderId = " + orderId);
    }

    public OrderNotFoundException(String status) {
        super("Any order Not Found for status = " + status);
    }

    public OrderNotFoundException(Long customerId, String message) {
        super(message + " for customerId = " + customerId);
    }

    public OrderNotFoundException(Long customerId, String status, String message) {
        super(message + ": " + status + " for customerId = " + customerId);
    }
}
