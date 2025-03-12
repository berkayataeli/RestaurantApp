package com.example.restaurantapp.exception;

public class IllegalUpdateStatusException extends RuntimeException {
    public IllegalUpdateStatusException(String nextState) {
        super("Invalid next state: " + nextState);
    }
}
