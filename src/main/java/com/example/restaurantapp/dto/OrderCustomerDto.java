package com.example.restaurantapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCustomerDto {

    private String customerName;
    private String customerSurname;
    private String phoneNumber;
    private String address;
    private Integer status;
    private Timestamp createdTime;
}
