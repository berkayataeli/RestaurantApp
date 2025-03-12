package com.example.restaurantapp.controller;

import com.example.restaurantapp.request.order.CreateOrderRequest;
import com.example.restaurantapp.request.order.ListOrderRequest;
import com.example.restaurantapp.request.order.UpdateOrderStatusRequest;
import com.example.restaurantapp.response.menu.OrderResponse;
import com.example.restaurantapp.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    //createOrder
    @PostMapping("/createOrder")
    public ResponseEntity<Void> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        orderService.createOrder(createOrderRequest);
        return ResponseEntity.ok().build();
    }

    //view order by orderId
    @PostMapping("/listOrdersByOrderId")
    public ResponseEntity<OrderResponse> listOrdersByOrderId(@RequestBody ListOrderRequest listOrderRequest) {
        return ResponseEntity.ok(orderService.listOrdersByOrderId(listOrderRequest));
    }

    //updateStatus
    @PostMapping("/updateOrderStatus")
    public ResponseEntity<Void> updateOrderStatus(@RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
        orderService.updateOrderStatus(updateOrderStatusRequest);
        return ResponseEntity.ok().build();
    }

}
