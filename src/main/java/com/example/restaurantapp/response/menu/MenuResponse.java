package com.example.restaurantapp.response.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {

    private String dayOfWeek;
    private List<FoodDto> foodList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodDto {
        private Long foodId;
        private String name;
        private String type;
        private String description;
        private BigDecimal price;
    }
}
