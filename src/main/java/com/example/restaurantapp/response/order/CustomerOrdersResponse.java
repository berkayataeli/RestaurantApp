package com.example.restaurantapp.response.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrdersResponse {
    private Long orderId;
    private String orderStatus;
    private String address;
    private String phoneNumber;
    private List<OrderResponse.FoodDetailResponse> foodDetailResponseList;
}
