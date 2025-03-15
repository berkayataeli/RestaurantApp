package com.example.restaurantapp.controller;

import com.example.restaurantapp.request.order.*;
import com.example.restaurantapp.response.order.OrderResponse;
import com.example.restaurantapp.response.order.OrderSearchResponse;
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

    @PostMapping("/search")
    public ResponseEntity<OrderSearchResponse> search(@RequestBody OrderSearchRequest orderSearchRequest) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(orderService.search(orderSearchRequest));
    }

    @PostMapping("/detail")
    public ResponseEntity<OrderResponse> detail(@RequestBody ListOrderRequest listOrderRequest) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(orderService.detail(listOrderRequest));
    }

    @PostMapping("/updateOrderStatus")
    public ResponseEntity<Void> updateOrderStatus(@RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
        orderService.updateOrderStatus(updateOrderStatusRequest);
        return ResponseEntity.ok().build();
    }

}
