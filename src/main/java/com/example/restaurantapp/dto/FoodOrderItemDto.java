package com.example.restaurantapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodOrderItemDto {
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
