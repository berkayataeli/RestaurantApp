package com.example.restaurantapp.controller;

import com.example.restaurantapp.enums.OrderStatus;
import com.example.restaurantapp.enums.UserTypeEnum;
import com.example.restaurantapp.request.order.ListOrderRequest;
import com.example.restaurantapp.request.order.OrderSearchRequest;
import com.example.restaurantapp.response.order.CustomerOrdersResponse;
import com.example.restaurantapp.response.order.OrderResponse;
import com.example.restaurantapp.response.order.OrderSearchResponse;
import com.example.restaurantapp.response.order.OrdersByStatusResponse;
import com.example.restaurantapp.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static com.example.restaurantapp.common.CommonUtils.asJsonString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void testListOrdersByOrderId_ShouldReturnOrderResponse_WhenRequestIsValid() throws Exception {
        Mockito.when(orderService.detail(any(ListOrderRequest.class)))
                .thenReturn(getDummyOrderResponse());

        mockMvc.perform(post("/api/order/detail")
                        .content(asJsonString(getDummyListOrderRequest())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private ListOrderRequest getDummyListOrderRequest() {
        ListOrderRequest listOrderRequest = new ListOrderRequest();
        listOrderRequest.setOrderId(1L);
        listOrderRequest.setUserType(UserTypeEnum.CUSTOMER.name());

        return listOrderRequest;
    }

    private OrderResponse getDummyOrderResponse() {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setStatus(OrderStatus.PREPARING.name());
        orderResponse.setCustomerName("Name");

        return orderResponse;
    }

    @Test
    void testSearchOrders_ShouldReturnSearchResponse_WhenRequestIsValid() throws Exception {
        Mockito.when(orderService.search(any(OrderSearchRequest.class)))
                .thenReturn(getDummyOrderSearchResponse());

        mockMvc.perform(post("/api/order/search")
                        .content(asJsonString(getDummyOrderSearchRequest())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private OrderSearchRequest getDummyOrderSearchRequest() {
        OrderSearchRequest orderSearchRequest = new OrderSearchRequest();
        orderSearchRequest.setCustomerId(1L);
        orderSearchRequest.setStatus(OrderStatus.PREPARING.name());
        return orderSearchRequest;
    }

    private OrderSearchResponse getDummyOrderSearchResponse() {
        OrdersByStatusResponse ordersByStatusResponse = new OrdersByStatusResponse();
        CustomerOrdersResponse customerOrdersResponse = new CustomerOrdersResponse();
        return new OrderSearchResponse(ordersByStatusResponse, customerOrdersResponse);
    }

}
