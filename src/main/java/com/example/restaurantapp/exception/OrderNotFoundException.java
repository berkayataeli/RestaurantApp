package com.example.restaurantapp.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("Order Not Found for orderId = " + orderId);
    }
}
