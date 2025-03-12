package com.example.restaurantapp.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PREPARING(0),
    DELIVERING(1),
    DELIVERED(2),
    CANCELLED(3);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }
}
