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
import com.example.restaurantapp.response.order.OrderSearchResponse;
import com.example.restaurantapp.response.order.OrdersByStatusResponse;
import com.example.restaurantapp.service.order.detail.CustomerListOrderService;
import com.example.restaurantapp.service.order.detail.RestaurantListOrderServiceDecorator;
import com.example.restaurantapp.service.order.search.OrderSearchSpecification;
import com.example.restaurantapp.service.order.status.OrderStatusContex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
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

    public OrderResponse detail(ListOrderRequest listOrderRequest) {
        log.info("detail called by : {}", listOrderRequest.getUserType());

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

    public OrderSearchResponse search(OrderSearchRequest orderSearchRequest) {
        log.info("search function request: {}", orderSearchRequest);
        boolean hasStatus = orderSearchRequest.getStatus() != null;
        boolean hasCustomerId = orderSearchRequest.getCustomerId() != null && orderSearchRequest.getCustomerId() != 0L;

        //get orders by dynamic filter
        List<Orders> orders = ordersRepository.findAll(getSpec(orderSearchRequest, hasCustomerId, hasStatus));

        //is there any order control
        if (orders.isEmpty()) {
            if (hasStatus && !hasCustomerId) {
                throw new OrderNotFoundException(orderSearchRequest.getStatus());
            } else if (!hasStatus && hasCustomerId) {
                throw new OrderNotFoundException(orderSearchRequest.getCustomerId(), "Customer has no orders");
            } else {
                throw new OrderNotFoundException(
                        orderSearchRequest.getCustomerId(),
                        orderSearchRequest.getStatus(),
                        "Customer has no orders with status"
                );
            }
        }

        //get orders
        List<Long> orderIds = orders.stream().map(Orders::getOrderId).toList();

        //has customerId then get customer infos by orders
        List<CustomersOrderDto> customersOrdersDtos = new java.util.ArrayList<>();
        if (hasCustomerId) {
            customersOrdersDtos = ordersRepository.findOrderCustomersByOrderIdInOrderByCreatedTime(orderIds);
        }

        //get orders food details matched with order id
        Map<Long, List<FoodOrderItemDto>> foodDetailResponseMap = getFoodDetailResponseList(orderIds);

        //generate dynamic response
        OrderSearchResponse orderSearchResponse = generateSearchResponse(orders, foodDetailResponseMap, customersOrdersDtos, hasCustomerId, hasStatus);


        log.info("search returned successfully for order status: {}, customerId: {}, response:{}", orderSearchRequest.getStatus(), orderSearchRequest.getCustomerId(), orderSearchResponse);
        return orderSearchResponse;
    }

    private Specification<Orders> getSpec(OrderSearchRequest orderSearchRequest, boolean hasCustomerId, boolean hasStatus) {
        Specification<Orders> spec = Specification.where(null);
        //dynamic filtering
        if (hasCustomerId) {
            spec = spec.and(OrderSearchSpecification.withCustomerId(orderSearchRequest.getCustomerId()));
        }
        if (hasStatus) {
            spec = spec.and(OrderSearchSpecification.withStatus(orderSearchRequest.getStatus()));
        }

        if (spec.equals(Specification.where(null)))
            throw new RuntimeException("No filter has been applied for search");

        return spec;
    }

    private OrderSearchResponse generateSearchResponse(List<Orders> orders, Map<Long, List<FoodOrderItemDto>> foodDetailResponseMap, List<CustomersOrderDto> customersOrdersDtos, boolean hasCustomerId, boolean hasStatus) {
        if (hasCustomerId && hasStatus) {
            return new OrderSearchResponse(
                    new OrdersByStatusResponse(createOrderDetailList(orders, foodDetailResponseMap)),
                    new CustomerOrdersResponse(createCustomerOrderDetailList(customersOrdersDtos, foodDetailResponseMap))
            );
        } else if (hasStatus) {
            return new OrderSearchResponse(
                    new OrdersByStatusResponse(createOrderDetailList(orders, foodDetailResponseMap))
            );
        }else {
            return new OrderSearchResponse(
                    new CustomerOrdersResponse(createCustomerOrderDetailList(customersOrdersDtos, foodDetailResponseMap))
            );
        }
    }

    private Map<Long, List<FoodOrderItemDto>> getFoodDetailResponseList(List<Long> orderIds) {
        return orderItemRepository.findFoodOrderItemsByOrderIdIn(orderIds).parallelStream()
                .collect(Collectors.groupingBy(FoodOrderItemDto::getOrderId));
    }

    private List<CustomerOrdersResponse.CustomerOrderDetail> createCustomerOrderDetailList(List<CustomersOrderDto> customersOrderDtos, Map<Long, List<FoodOrderItemDto>> foodOrderItemDtoMap) {
        return customersOrderDtos.stream()
                .map(customersOrderDto -> orderMapper.customerOrderDetailMapper(
                        customersOrderDto,
                        foodOrderItemDtoMap.get(customersOrderDto.getOrderId())
                ))
                .toList();
    }

    private List<OrdersByStatusResponse.OrderDetails> createOrderDetailList(List<Orders> orders, Map<Long, List<FoodOrderItemDto>> foodDetailResponseMap) {
        return orders.stream()
                .map(order -> orderMapper.mapOrderToOrderDetails(order, foodDetailResponseMap.get(order.getOrderId())))
                .toList();
    }
}
