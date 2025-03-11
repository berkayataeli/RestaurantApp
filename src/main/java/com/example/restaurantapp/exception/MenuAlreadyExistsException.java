package com.example.restaurantapp.exception;

public class MenuAlreadyExistsException extends RuntimeException {
    public MenuAlreadyExistsException(String message) {
        super(message);
    }
}
