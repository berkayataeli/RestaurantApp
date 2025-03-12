package com.example.restaurantapp.service.order.status;

import com.example.restaurantapp.enums.OrderStatus;
import lombok.Setter;

@Setter
public class OrderStatusContex {

    private OrderStatusState state;

    public OrderStatusContex(Integer currentState) {
        if (OrderStatus.PREPARING.getValue() == currentState)
            this.state = new PreparingState();
        else if (OrderStatus.DELIVERING.getValue() == currentState)
            this.state = new DeliveringState();
        else if (OrderStatus.DELIVERED.getValue() == currentState)
            this.state = new DeliveredState();
        else
            this.state = new CanceledState();
    }

    public void updateOrderStatus(String nextState) {
        state.updateOrderStatus(this, nextState);
    }

}
