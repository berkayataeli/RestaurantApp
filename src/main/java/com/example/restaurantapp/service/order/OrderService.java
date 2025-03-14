package com.example.restaurantapp.service.order;

import com.example.restaurantapp.dataaccess.CustomerRepository;
import com.example.restaurantapp.dataaccess.OrderItemRepository;
import com.example.restaurantapp.dataaccess.OrdersRepository;
import com.example.restaurantapp.dbmodel.OrderItem;
import com.example.restaurantapp.dbmodel.Orders;
import com.example.restaurantapp.dto.CustomersOrderDto;
import com.example.restaurantapp.dto.FoodOrderItemDto;
import com.example.restaurantapp.enums.OrderStatus;
import com.example.restaurantapp.enums.UserTypeEnum;
import com.example.restaurantapp.exception.IllegalUpdateStatusException;
import com.example.restaurantapp.exception.OrderNotFoundException;
import com.example.restaurantapp.mapper.OrderMapper;
import com.example.restaurantapp.request.order.*;
import com.example.restaurantapp.response.order.CustomerOrdersResponse;
import com.example.restaurantapp.response.order.OrderResponse;
import com.example.restaurantapp.response.order.OrdersByStatusResponse;
import com.example.restaurantapp.service.order.list.CustomerListOrderService;
import com.example.restaurantapp.service.order.list.RestaurantListOrderServiceDecorator;
import com.example.restaurantapp.service.order.status.OrderStatusContex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<OrderItem> orderItems = createOrderRequest.getOrderDetailList().stream().map(orderDetail -> orderMapper.createOrderItemMapper(orderId, orderDetail)).toList();
        orderItemRepository.saveAll(orderItems);

        log.info("Order created successfully");
    }

    public OrderResponse listOrdersByOrderId(ListOrderRequest listOrderRequest) {
        log.info("listOrdersByOrderId called by : {}", listOrderRequest.getUserType());

        return UserTypeEnum.CUSTOMER.name().equals(listOrderRequest.getUserType()) ?
                customerListOrderService.listOrdersByOrderId(listOrderRequest.getOrderId())
                : restaurantListOrderServiceDecorator.listOrdersByOrderId(listOrderRequest.getOrderId());
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

    public OrdersByStatusResponse listOrdersByOrderStatus(ListOrderByStatusRequest listOrderByStatusRequest) {
        log.info("listOrdersByOrderStatus function request: {}", listOrderByStatusRequest);
        // get orders by status
        List<Orders> orders = ordersRepository.findAllByStatusOrderByCreatedTimeDesc(OrderStatus.valueOf(listOrderByStatusRequest.getOrderStatus()).getValue());

        if(orders == null || orders.isEmpty())
            throw new OrderNotFoundException(listOrderByStatusRequest.getOrderStatus());

        // get all food datas and create a map by matching food data with orders
        List<Long> orderIds = orders.stream().map(Orders::getOrderId).toList();
        Map<Long, List<FoodOrderItemDto>> foodDetailResponseMap = getFoodDetailResponseList(orderIds);

        // generate order response with food details
        List<OrdersByStatusResponse.OrderDetails> orderResponsesByStatus = orders.stream()
                .map(order -> orderMapper.mapOrderToOrderDetails(order, foodDetailResponseMap.get(order.getOrderId())))
                .toList();

        log.info("listOrdersByOrderStatus returned successfully for order status: {}", listOrderByStatusRequest.getOrderStatus());
        return new OrdersByStatusResponse(orderResponsesByStatus);
    }

    public CustomerOrdersResponse listOrdersByCustomerId(CustomerOrdersRequest customerOrdersRequest) {
        log.info("listOrdersByCustomerId function request: {}", customerOrdersRequest);
        // get all orders that the customer has
        List<Orders> orders = ordersRepository.findAllByCustomerId(customerOrdersRequest.getCustomerId());

        if(orders == null || orders.isEmpty())
            throw new OrderNotFoundException(customerOrdersRequest.getCustomerId(), "Customer has no orders");

        // get all orders ids and get customer infos
        List<Long> orderIds = orders.stream().map(Orders::getOrderId).toList();
        List<CustomersOrderDto> customersOrdersDtos = ordersRepository.findOrderCustomersByOrderIdInOrderByCreatedTime(orderIds);

        // get all food datas and create a map by matching food data with orders
        Map<Long, List<FoodOrderItemDto>> foodDetailResponseMap = getFoodDetailResponseList(orderIds);

        // generate CustomerOrderResponse with customersOrdersDtos and foodOrderItemMap
        List<CustomerOrdersResponse.CustomerOrderDetail> customerOrderDetails =
                customersOrdersDtos.stream()
                        .map(customersOrderDto -> orderMapper.customerOrderDetailMapper(
                                customersOrderDto,
                                foodDetailResponseMap.get(customersOrderDto.getOrderId())
                        ))
                        .collect(Collectors.toList());

        log.info("listOrdersByCustomerId returned successfully for customerId: {}", customerOrdersRequest.getCustomerId());
        return new CustomerOrdersResponse(customerOrderDetails);
    }

    Map<Long, List<FoodOrderItemDto>> getFoodDetailResponseList(List<Long> orderIds) {
        return orderItemRepository.findFoodOrderItemsByOrderIdIn(orderIds).parallelStream()
                .collect(Collectors.groupingBy(FoodOrderItemDto::getOrderId));
    }
}
