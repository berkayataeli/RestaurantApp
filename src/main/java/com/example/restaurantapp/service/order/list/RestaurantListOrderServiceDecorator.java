package com.example.restaurantapp.service.order.list;

import com.example.restaurantapp.dataaccess.OrdersRepository;
import com.example.restaurantapp.request.order.ListOrderRequest;
import com.example.restaurantapp.response.menu.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class RestaurantListOrderServiceDecorator extends ListOrderServiceDecorator {

    @Autowired
    private OrdersRepository ordersRepository;

    public RestaurantListOrderServiceDecorator(CustomerListOrderService customerListOrderService) {
        super(customerListOrderService);
    }

    @Override
    public OrderResponse listOrdersByOrderId(ListOrderRequest listOrderRequest){
        log.info("Restaurant listOrdersByOrderId function request: {}", listOrderRequest);
        OrderResponse orderResponse = super.listOrdersByOrderId(listOrderRequest);

        //get customer's numberOfOrder info for restaurant user
        Integer numberOfOrder = ordersRepository.findOrderCustomerByOrderId(listOrderRequest.getOrderId()).getNumberOfOrder();
        orderResponse.setNumberOfOrder(numberOfOrder);
        log.info("Restaurant Order returned by {} response {}, ", listOrderRequest.getOrderId(), orderResponse);
        return orderResponse;
    }

}
