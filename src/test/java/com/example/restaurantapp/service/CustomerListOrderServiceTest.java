package com.example.restaurantapp.service;

import com.example.restaurantapp.dataaccess.OrderItemRepository;
import com.example.restaurantapp.dataaccess.OrdersRepository;
import com.example.restaurantapp.dto.FoodOrderItemDto;
import com.example.restaurantapp.dto.OrderCustomerDto;
import com.example.restaurantapp.enums.UserTypeEnum;
import com.example.restaurantapp.exception.OrderNotFoundException;
import com.example.restaurantapp.mapper.OrderMapper;
import com.example.restaurantapp.request.order.ListOrderRequest;
import com.example.restaurantapp.response.order.OrderResponse;
import com.example.restaurantapp.service.order.detail.CustomerListOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(SpringExtension.class)
public class CustomerListOrderServiceTest {

    @InjectMocks
    private CustomerListOrderService customerListOrderService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Test
    void listOrdersByOrderId_ShouldThrowOrderNotFoundException_WhenOrderDoesNotExist() {
        ListOrderRequest request = new ListOrderRequest();
        request.setUserType(UserTypeEnum.CUSTOMER.name());
        request.setOrderId(123L);

        when(ordersRepository.findOrderCustomerByOrderId(request.getOrderId())).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> customerListOrderService.listOrdersByOrderId(request.getOrderId()));

        verify(ordersRepository, times(1)).findOrderCustomerByOrderId(request.getOrderId());
        verifyNoInteractions(orderItemRepository);
    }

    @Test
    void listOrdersByOrderId_ShouldReturnOrderResponse_WhenOrderExists() {
        Long orderId = 1L;

        OrderCustomerDto orderCustomerDto = new OrderCustomerDto("John", "Doe", "1234567890", "123 Street", 1, null);
        when(ordersRepository.findOrderCustomerByOrderId(orderId)).thenReturn(Optional.of(orderCustomerDto));

        FoodOrderItemDto foodOrderItemDto = new FoodOrderItemDto(1L, "Pizza", new BigDecimal("12.5"), 2);
        when(orderItemRepository.findFoodOrderItemsByOrderId(orderId)).thenReturn(List.of(foodOrderItemDto));

        OrderResponse.FoodDetailResponse foodDetailResponse = new OrderResponse.FoodDetailResponse("Pizza",2, new BigDecimal("12.5"));
        when(orderMapper.foodDetailResponseMapper(foodOrderItemDto)).thenReturn(foodDetailResponse);

        OrderResponse expectedResponse = new OrderResponse();
        expectedResponse.setCustomerName("John");
        expectedResponse.setFoodDetailResponseList(List.of(foodDetailResponse));
        when(orderMapper.orderResponseMapper(orderCustomerDto, List.of(foodDetailResponse))).thenReturn(expectedResponse);

        OrderResponse actualResponse = customerListOrderService.listOrdersByOrderId(orderId);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getCustomerName(), actualResponse.getCustomerName());
        assertEquals(expectedResponse.getFoodDetailResponseList().size(), actualResponse.getFoodDetailResponseList().size());
        verify(ordersRepository, times(1)).findOrderCustomerByOrderId(orderId);
        verify(orderItemRepository, times(1)).findFoodOrderItemsByOrderId(orderId);
        verify(orderMapper, times(1)).orderResponseMapper(orderCustomerDto, List.of(foodDetailResponse));
    }

}
