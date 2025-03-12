package com.example.restaurantapp.exception;

public class MenuNotFoundException extends RuntimeException {
    public MenuNotFoundException(String dayOfWeek) {
        super("Menu for day '" + dayOfWeek + "' does not exist!");
    }
}
