package com.example.restaurantapp.service;

import com.example.restaurantapp.dataaccess.CustomerRepository;
import com.example.restaurantapp.dataaccess.OrderItemRepository;
import com.example.restaurantapp.dataaccess.OrdersRepository;
import com.example.restaurantapp.dbmodel.OrderItem;
import com.example.restaurantapp.dbmodel.Orders;
import com.example.restaurantapp.dto.OrderCustomerDto;
import com.example.restaurantapp.enums.OrderStatus;
import com.example.restaurantapp.enums.UserTypeEnum;
import com.example.restaurantapp.exception.IllegalUpdateStatusException;
import com.example.restaurantapp.exception.OrderNotFoundException;
import com.example.restaurantapp.mapper.OrderMapper;
import com.example.restaurantapp.request.order.CreateOrderRequest;
import com.example.restaurantapp.request.order.ListOrderRequest;
import com.example.restaurantapp.request.order.UpdateOrderStatusRequest;
import com.example.restaurantapp.response.order.OrderResponse;
import com.example.restaurantapp.service.order.OrderService;
import com.example.restaurantapp.service.order.list.CustomerListOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CustomerListOrderService customerListOrderService;

    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        CreateOrderRequest.OrderDetail orderDetail = new CreateOrderRequest.OrderDetail();
        orderDetail.setFoodId(1L);
        orderDetail.setQuantity(1);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(1L);
        request.setOrderDetailList(Collections.singletonList(orderDetail));

        Orders newOrder = new Orders();
        newOrder.setCustomerId(request.getCustomerId());
        newOrder.setStatus(0);
        newOrder.setCreatedTime(new Timestamp(System.currentTimeMillis()));

        Orders savedOrder = new Orders();
        savedOrder.setOrderId(100L);
        savedOrder.setCustomerId(request.getCustomerId());
        savedOrder.setStatus(0);
        savedOrder.setCreatedTime(newOrder.getCreatedTime());

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(savedOrder.getOrderId());
        orderItem.setFoodId(orderDetail.getFoodId());
        orderItem.setQuantity(orderDetail.getQuantity());

        when(orderMapper.createOrdersMapper(request.getCustomerId())).thenReturn(newOrder);
        when(ordersRepository.findTopByCustomerIdOrderByCreatedTimeDesc(request.getCustomerId())).thenReturn(savedOrder);
        when(orderMapper.createOrderItemMapper(savedOrder.getOrderId(), orderDetail)).thenReturn(orderItem);

        orderService.createOrder(request);

        verify(ordersRepository, times(1)).save(any(Orders.class));
        verify(ordersRepository, times(1)).findTopByCustomerIdOrderByCreatedTimeDesc(request.getCustomerId());
        verify(orderItemRepository, times(1)).saveAll(Collections.singletonList(orderItem));
    }

    @Test
    public void testListOrdersByOrderId_ShouldReturnOrderList_WhenOrdersExist() {
        OrderCustomerDto orderCustomerDto = new OrderCustomerDto();
        orderCustomerDto.setCustomerName("John");

        when(ordersRepository.findOrderCustomerByOrderId(1L)).thenReturn(Optional.of(orderCustomerDto));

        ListOrderRequest listOrderRequest = new ListOrderRequest();
        listOrderRequest.setOrderId(1L);
        listOrderRequest.setUserType(UserTypeEnum.CUSTOMER.name());

        OrderResponse expectedResponse = new OrderResponse();
        expectedResponse.setStatus(OrderStatus.PREPARING.name());
        expectedResponse.setCustomerName("John");

        when(orderService.listOrdersByOrderId(listOrderRequest)).thenReturn(expectedResponse);

        OrderResponse actualResponse = orderService.listOrdersByOrderId(listOrderRequest);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getCustomerName(), actualResponse.getCustomerName());
    }

    @Test
    void testUpdateOrderStatus_Success() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(123L);
        request.setOrderStatus("DELIVERING");

        Orders mockOrder = new Orders();
        mockOrder.setOrderId(123L);
        mockOrder.setStatus(OrderStatus.PREPARING.getValue());
        mockOrder.setCustomerId(456L);

        when(ordersRepository.findById(request.getOrderId())).thenReturn(Optional.of(mockOrder));

        orderService.updateOrderStatus(request);

        verify(ordersRepository, times(1)).saveAndFlush(mockOrder);
        assertEquals(OrderStatus.DELIVERING.getValue(), mockOrder.getStatus());
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(123L);
        request.setOrderStatus("CANCELLED");

        when(ordersRepository.findById(request.getOrderId())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrderStatus(request));
        verify(ordersRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUpdateOrderStatus_InvalidStatusTransition() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(123L);
        request.setOrderStatus("DELIVERED");

        Orders mockOrder = new Orders();
        mockOrder.setOrderId(123L);
        mockOrder.setStatus(OrderStatus.CANCELLED.getValue());

        when(ordersRepository.findById(request.getOrderId())).thenReturn(Optional.of(mockOrder));

        assertThrows(IllegalUpdateStatusException.class, () -> orderService.updateOrderStatus(request));
        verify(ordersRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUpdateOrderStatus_IncrementsOrdersIfDelivered() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(123L);
        request.setOrderStatus("DELIVERED");

        Orders mockOrder = new Orders();
        mockOrder.setOrderId(123L);
        mockOrder.setCustomerId(456L);
        mockOrder.setStatus(OrderStatus.DELIVERING.getValue());

        when(ordersRepository.findById(request.getOrderId())).thenReturn(Optional.of(mockOrder));

        orderService.updateOrderStatus(request);

        verify(customerRepository, times(1)).incrementNumberOfOrderNative(mockOrder.getCustomerId());
        verify(ordersRepository, times(1)).saveAndFlush(mockOrder);
    }



}
