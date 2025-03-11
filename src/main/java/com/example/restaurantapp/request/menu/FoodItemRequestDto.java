package com.example.restaurantapp.request.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemRequestDto {
    private String name;
    private String type;
    private String description;
    private Double price;
}
