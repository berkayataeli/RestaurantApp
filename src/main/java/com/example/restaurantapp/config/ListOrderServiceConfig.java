package com.example.restaurantapp.config;

import com.example.restaurantapp.dataaccess.OrderItemRepository;
import com.example.restaurantapp.dataaccess.OrdersRepository;
import com.example.restaurantapp.mapper.OrderMapper;
import com.example.restaurantapp.service.order.detail.CustomerListOrderService;
import com.example.restaurantapp.service.order.detail.RestaurantListOrderServiceDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListOrderServiceConfig {

    @Bean
    public CustomerListOrderService customerListOrderService(OrdersRepository ordersRepository, OrderItemRepository orderItemRepository, OrderMapper orderMapper) {
        // Default service response for Customer
        return new CustomerListOrderService(ordersRepository, orderItemRepository, orderMapper);
    }

    @Bean
    public RestaurantListOrderServiceDecorator restaurantListOrderServiceDecorator(CustomerListOrderService customerListOrderService) {
        // Restaurant expand on customer
        return new RestaurantListOrderServiceDecorator(customerListOrderService);
    }

}
