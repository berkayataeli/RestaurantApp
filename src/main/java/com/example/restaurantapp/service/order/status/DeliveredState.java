package com.example.restaurantapp.service.order.status;

import com.example.restaurantapp.exception.IllegalUpdateStatusException;

public class DeliveredState implements OrderStatusState {

    @Override
    public void updateOrderStatus(OrderStatusContex orderStatusContex, String nextState) {
        throw new IllegalUpdateStatusException(nextState);
    }
}
