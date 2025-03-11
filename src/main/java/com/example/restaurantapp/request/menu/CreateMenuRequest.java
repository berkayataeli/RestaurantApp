package com.example.restaurantapp.request.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMenuRequest {

    private List<MenuItem> menuItemList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuItem {
        private String dayOfWeek;
        private List<FoodItemRequestDto> foodItemRequestList;
    }
}
