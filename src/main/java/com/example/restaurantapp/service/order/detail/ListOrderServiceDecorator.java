package com.example.restaurantapp.service.order.detail;

import com.example.restaurantapp.response.order.OrderResponse;

public abstract class ListOrderServiceDecorator extends CustomerListOrderService {

    protected final CustomerListOrderService customerListOrderService;

    public ListOrderServiceDecorator(CustomerListOrderService customerListOrderService) {
        super(customerListOrderService.ordersRepository, customerListOrderService.orderItemRepository, customerListOrderService.orderMapper);
        this.customerListOrderService = customerListOrderService;
    }

    @Override
    public OrderResponse listOrdersByOrderId(Long orderId) {
        return customerListOrderService.listOrdersByOrderId(orderId);
    }
}
