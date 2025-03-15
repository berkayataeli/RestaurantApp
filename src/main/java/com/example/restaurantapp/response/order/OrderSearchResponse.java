package com.example.restaurantapp.response.order;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderSearchResponse {
    private OrdersByStatusResponse ordersByStatus;
    private CustomerOrdersResponse customerOrders;

    public OrderSearchResponse(OrdersByStatusResponse ordersByStatus) {
        this.ordersByStatus = ordersByStatus;
    }

    public OrderSearchResponse(CustomerOrdersResponse customerOrders) {
        this.customerOrders = customerOrders;
    }

    public OrderSearchResponse(OrdersByStatusResponse ordersByStatus, CustomerOrdersResponse customerOrders) {
        this.ordersByStatus = ordersByStatus;
        this.customerOrders = customerOrders;
    }
}
