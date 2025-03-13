package com.example.restaurantapp.service.order;

import com.example.restaurantapp.dataaccess.CustomerRepository;
import com.example.restaurantapp.dataaccess.OrderItemRepository;
import com.example.restaurantapp.dataaccess.OrdersRepository;
import com.example.restaurantapp.dbmodel.OrderItem;
import com.example.restaurantapp.dbmodel.Orders;
import com.example.restaurantapp.enums.OrderStatus;
import com.example.restaurantapp.enums.UserTypeEnum;
import com.example.restaurantapp.exception.IllegalUpdateStatusException;
import com.example.restaurantapp.exception.OrderNotFoundException;
import com.example.restaurantapp.mapper.OrderMapper;
import com.example.restaurantapp.request.order.CreateOrderRequest;
import com.example.restaurantapp.request.order.ListOrderRequest;
import com.example.restaurantapp.request.order.UpdateOrderStatusRequest;
import com.example.restaurantapp.response.menu.OrderResponse;
import com.example.restaurantapp.service.order.list.CustomerListOrderService;
import com.example.restaurantapp.service.order.list.RestaurantListOrderServiceDecorator;
import com.example.restaurantapp.service.order.status.OrderStatusContex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;
    private final CustomerListOrderService customerListOrderService;
    private final RestaurantListOrderServiceDecorator restaurantListOrderServiceDecorator;

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
        log.info("listOrdersByOrderId called by : {}", listOrderRequest.getUserType());

        return UserTypeEnum.CUSTOMER.name().equals(listOrderRequest.getUserType()) ?
                customerListOrderService.listOrdersByOrderId(listOrderRequest)
                : restaurantListOrderServiceDecorator.listOrdersByOrderId(listOrderRequest);
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
        orders.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
        ordersRepository.saveAndFlush(orders);

        // increment foods number of order if status is delivered
        if (OrderStatus.DELIVERED.name().equals(updateOrderStatusRequest.getOrderStatus())) {
            incrementNumberOfOrder(orders.getCustomerId());
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

    private void incrementNumberOfOrder(Long customerId) {
        customerRepository.incrementNumberOfOrderNative(customerId);
        log.info("Number of order incremented successfully for customer id: {}", customerId);
    }
}
