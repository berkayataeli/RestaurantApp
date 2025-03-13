package com.example.restaurantapp.service;

import com.example.restaurantapp.controller.OrderController;
import com.example.restaurantapp.dataaccess.OrderItemRepository;
import com.example.restaurantapp.dataaccess.OrdersRepository;
import com.example.restaurantapp.dbmodel.OrderItem;
import com.example.restaurantapp.dbmodel.Orders;
import com.example.restaurantapp.dto.OrderCustomerDto;
import com.example.restaurantapp.enums.OrderStatus;
import com.example.restaurantapp.enums.UserTypeEnum;
import com.example.restaurantapp.request.order.CreateOrderRequest;
import com.example.restaurantapp.request.order.ListOrderRequest;
import com.example.restaurantapp.response.menu.OrderResponse;
import com.example.restaurantapp.service.order.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderService orderService;


    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    public void testListOrdersByOrderId_ShouldReturnOrderList_WhenOrdersExist() {
        Orders order = new Orders();
        order.setStatus(0);
        order.setCustomerId(1L);
        order.setCreatedTime(new Timestamp(System.currentTimeMillis()));

        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderCustomerDto orderCustomerDto = new OrderCustomerDto();
        orderCustomerDto.setCustomerName("John Doe");
        when(ordersRepository.findOrderCustomerByOrderId(1L)).thenReturn(orderCustomerDto);

        ListOrderRequest listOrderRequest = new ListOrderRequest();
        listOrderRequest.setOrderId(1L);
        listOrderRequest.setUserType(UserTypeEnum.CUSTOMER.name());

        OrderResponse expectedResponse = new OrderResponse();
        expectedResponse.setStatus(OrderStatus.PREPARING.name());
        expectedResponse.setCustomerName("John Doe");
        when(orderService.listOrdersByOrderId(listOrderRequest)).thenReturn(expectedResponse);

        OrderResponse actualResponse = orderService.listOrdersByOrderId(listOrderRequest);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getCustomerName(), actualResponse.getCustomerName());
    }

    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(1L);

        Orders newOrder = new Orders();
        newOrder.setCustomerId(request.getCustomerId());
        newOrder.setStatus(0);
        newOrder.setCreatedTime(new Timestamp(System.currentTimeMillis()));

        Orders savedOrder = new Orders();
        savedOrder.setOrderId(100L);
        savedOrder.setCustomerId(request.getCustomerId());
        savedOrder.setStatus(0);
        savedOrder.setCreatedTime(newOrder.getCreatedTime());

        when(ordersRepository.save(any(Orders.class))).thenReturn(savedOrder);

        orderService.createOrder(request);

        verify(ordersRepository, times(1)).save(any(Orders.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenRequestIsInvalid() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(null);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));

        verifyNoInteractions(ordersRepository);
    }


}
