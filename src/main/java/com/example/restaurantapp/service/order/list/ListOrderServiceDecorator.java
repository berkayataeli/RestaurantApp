package com.example.restaurantapp.service.order.list;

import com.example.restaurantapp.request.order.ListOrderRequest;
import com.example.restaurantapp.response.menu.OrderResponse;

public abstract class ListOrderServiceDecorator extends CustomerListOrderService {

    protected final CustomerListOrderService customerListOrderService;

    public ListOrderServiceDecorator(CustomerListOrderService customerListOrderService) {
        super(customerListOrderService.ordersRepository, customerListOrderService.orderItemRepository, customerListOrderService.orderMapper);
        this.customerListOrderService = customerListOrderService;
    }

    @Override
    public OrderResponse listOrdersByOrderId(ListOrderRequest listOrderRequest) {
        return customerListOrderService.listOrdersByOrderId(listOrderRequest);
    }
}
