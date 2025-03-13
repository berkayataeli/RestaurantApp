package com.example.restaurantapp.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOrderRequest {
    private String userType;
    private Long orderId;
}
