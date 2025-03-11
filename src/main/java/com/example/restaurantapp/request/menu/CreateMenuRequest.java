package com.example.restaurantapp.request.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMenuRequest {

    private String dayOfWeek;
    private List<FoodItemRequest> foodItemRequestList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodItemRequest {
        private String name;
        private String type;
        private String description;
        private Double price;
    }

}
