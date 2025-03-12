package com.example.restaurantapp.service.order.status;

import com.example.restaurantapp.enums.OrderStatus;
import com.example.restaurantapp.exception.IllegalUpdateStatusException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PreparingState implements OrderStatusState {

    @Override
    public void updateOrderStatus(OrderStatusContex orderStatusContex, String nextState) {
        if (OrderStatus.DELIVERING.name().equals(nextState))
            orderStatusContex.setState(new DeliveringState());
        else if (OrderStatus.CANCELLED.name().equals(nextState))
            orderStatusContex.setState(new CanceledState());
        else
            throw new IllegalUpdateStatusException("Invalid next state: " + nextState);

        log.info("Order status updated to: {}", nextState);
    }
}
