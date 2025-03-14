package com.example.restaurantapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomersOrderDto {

    private Long orderId;
    private Integer status;
    private String address;
    private String phoneNumber;
}
