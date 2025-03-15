package com.example.restaurantapp.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchRequest {
    private Long customerId;
    private String status;
}
