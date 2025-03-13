package com.example.restaurantapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCustomerDto {

    private Integer numberOfOrder;
    private String customerName;
    private String customerSurname;
    private String phoneNumber;
    private String address;
    private Integer status;
}
