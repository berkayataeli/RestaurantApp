package com.example.restaurantapp.service.order;

import com.example.restaurantapp.dataaccess.FoodRepository;
import com.example.restaurantapp.dataaccess.OrderItemRepository;
import com.example.restaurantapp.dataaccess.OrdersRepository;
import com.example.restaurantapp.dbmodel.OrderItem;
import com.example.restaurantapp.dbmodel.Orders;
import com.example.restaurantapp.dto.FoodOrderItemDto;
import com.example.restaurantapp.dto.OrderCustomerDto;
import com.example.restaurantapp.enums.OrderStatus;
import com.example.restaurantapp.exception.IllegalUpdateStatusException;
import com.example.restaurantapp.exception.OrderNotFoundException;
import com.example.restaurantapp.mapper.OrderMapper;
import com.example.restaurantapp.request.order.CreateOrderRequest;
import com.example.restaurantapp.request.order.ListOrderRequest;
import com.example.restaurantapp.request.order.UpdateOrderStatusRequest;
import com.example.restaurantapp.response.menu.OrderResponse;
import com.example.restaurantapp.service.order.status.OrderStatusContex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final FoodRepository foodRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public void createOrder(CreateOrderRequest createOrderRequest) {
        log.info("Create order function request: {}", createOrderRequest);

        // generate and insert orders by request
        Orders orders = orderMapper.createOrdersMapper(createOrderRequest.getCustomerId());
        ordersRepository.save(orders);

        // generate and insert order item by request and orders
        Long orderId = ordersRepository.findTopByCustomerIdOrderByCreatedTimeDesc(createOrderRequest.getCustomerId()).getOrderId();
        OrderItem orderItem = orderMapper.createOrderItemMapper(orderId, createOrderRequest);
        orderItemRepository.save(orderItem);

        log.info("Order created successfully");
    }

    public OrderResponse listOrdersByOrderId(ListOrderRequest listOrderRequest) {
        log.info("listOrdersByOrderId function request: {}", listOrderRequest);

        //get Orders status and customer details infos
        OrderCustomerDto orderCustomerDto = ordersRepository.findOrderCustomerByOrderId(listOrderRequest.getOrderId());

        //get food and order_item details
        List<FoodOrderItemDto> foodOrderItemDTOList = orderItemRepository.findFoodOrderItemsByOrderId(listOrderRequest.getOrderId());

        //generate OrderResponse
        List<OrderResponse.FoodDetailResponse> foodDetailResponseList = foodOrderItemDTOList.stream().map(orderMapper::foodDetailResponseMapper).toList();
        OrderResponse orderResponse = orderMapper.orderResponseMapper(orderCustomerDto, foodDetailResponseList);

        log.info("Order returned by {} response {}, ", listOrderRequest.getOrderId(), orderResponse);
        return orderResponse;
    }

    @Transactional
    public void updateOrderStatus(UpdateOrderStatusRequest updateOrderStatusRequest) {
        log.info("updateOrderStatus function request: {}", updateOrderStatusRequest);

        // get Orders by order_id
        Orders orders = ordersRepository.findById(updateOrderStatusRequest.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(updateOrderStatusRequest.getOrderId()));

        // check updating status is legal
        checkNextStatusLegal(orders.getStatus(), updateOrderStatusRequest.getOrderStatus());

        // save and flush after updated new status
        orders.setStatus(OrderStatus.valueOf(updateOrderStatusRequest.getOrderStatus()).getValue());
        ordersRepository.saveAndFlush(orders);

        // increment foods number of order if status is delivered
        if (OrderStatus.DELIVERED.name().equals(updateOrderStatusRequest.getOrderStatus())) {
            incrementNumberOfOrder(orders.getOrderId());
        }
        log.info("Order status updated successfully for order id: {}", updateOrderStatusRequest.getOrderId());
    }

    private void checkNextStatusLegal(Integer currentStatus, String nextStatus) {
        try {
            OrderStatusContex orderStatusContex = new OrderStatusContex(currentStatus);
            orderStatusContex.updateOrderStatus(nextStatus);
        } catch (IllegalUpdateStatusException e) {
            throw new IllegalUpdateStatusException(nextStatus);
        }
    }

    private void incrementNumberOfOrder(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        List<Long> foodIds = orderItems.stream().map(OrderItem::getFoodId).toList();
        foodRepository.incrementNumberOfOrderForFoods(foodIds);
        log.info("Food number of order incremented successfully for order id: {}", foodIds);
    }
}
