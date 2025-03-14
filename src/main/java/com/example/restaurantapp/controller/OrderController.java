package com.example.restaurantapp.controller;

import com.example.restaurantapp.request.order.*;
import com.example.restaurantapp.response.order.CustomerOrdersResponse;
import com.example.restaurantapp.response.order.OrderResponse;
import com.example.restaurantapp.response.order.OrdersByStatusResponse;
import com.example.restaurantapp.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/createOrder")
    public ResponseEntity<Void> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        orderService.createOrder(createOrderRequest);
        return ResponseEntity.ok().build();
    }

    //list orders function for restaurant user
    @PostMapping("/listOrdersByOrderStatus")
    public ResponseEntity<OrdersByStatusResponse> listOrdersByOrderStatus(@RequestBody ListOrderByStatusRequest listOrderByStatusRequest) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(orderService.listOrdersByOrderStatus(listOrderByStatusRequest));
    }

    //list orders function for customer user
    @PostMapping("/listOrdersByCustomerId")
    public ResponseEntity<CustomerOrdersResponse> listOrdersByCustomerId(@RequestBody CustomerOrdersRequest customerOrdersRequest) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(orderService.listOrdersByCustomerId(customerOrdersRequest));
    }

    @PostMapping("/orderDetail")
    public ResponseEntity<OrderResponse> listOrdersByOrderId(@RequestBody ListOrderRequest listOrderRequest) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(orderService.listOrdersByOrderId(listOrderRequest));
    }

    @PostMapping("/updateOrderStatus")
    public ResponseEntity<Void> updateOrderStatus(@RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
        orderService.updateOrderStatus(updateOrderStatusRequest);
        return ResponseEntity.ok().build();
    }

}
