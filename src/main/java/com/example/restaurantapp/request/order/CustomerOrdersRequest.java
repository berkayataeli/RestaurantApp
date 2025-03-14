package com.example.restaurantapp.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrdersRequest {
    private Long customerId;
}
