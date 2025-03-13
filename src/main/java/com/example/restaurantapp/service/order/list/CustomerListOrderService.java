package com.example.restaurantapp.service.order.list;

import com.example.restaurantapp.dataaccess.OrderItemRepository;
import com.example.restaurantapp.dataaccess.OrdersRepository;
import com.example.restaurantapp.dto.FoodOrderItemDto;
import com.example.restaurantapp.dto.OrderCustomerDto;
import com.example.restaurantapp.mapper.OrderMapper;
import com.example.restaurantapp.request.order.ListOrderRequest;
import com.example.restaurantapp.response.menu.OrderResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CustomerListOrderService {

    protected final OrdersRepository ordersRepository;
    protected final OrderItemRepository orderItemRepository;
    protected final OrderMapper orderMapper;

    public CustomerListOrderService(OrdersRepository ordersRepository, OrderItemRepository orderItemRepository, OrderMapper orderMapper) {
        this.ordersRepository = ordersRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
    }

    public OrderResponse listOrdersByOrderId(ListOrderRequest listOrderRequest) {
        log.info("Customer listOrdersByOrderId function request: {}", listOrderRequest);
        //get Orders status and customer details infos
        OrderCustomerDto orderCustomerDto = ordersRepository.findOrderCustomerByOrderId(listOrderRequest.getOrderId());

        //get food and order_item details
        List<FoodOrderItemDto> foodOrderItemDTOList = orderItemRepository.findFoodOrderItemsByOrderId(listOrderRequest.getOrderId());

        //generate OrderResponse
        List<OrderResponse.FoodDetailResponse> foodDetailResponseList = foodOrderItemDTOList.stream().map(orderMapper::foodDetailResponseMapper).toList();
        OrderResponse orderResponse = orderMapper.orderResponseMapper(orderCustomerDto, foodDetailResponseList);

        log.info("Customer Order returned by {} response {}, ", listOrderRequest.getOrderId(), orderResponse);
        return orderResponse;
    }
}
