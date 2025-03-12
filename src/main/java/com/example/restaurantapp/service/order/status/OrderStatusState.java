package com.example.restaurantapp.service.order.status;

public interface OrderStatusState {
    void updateOrderStatus(OrderStatusContex orderStatusContex, String nextState);
}
