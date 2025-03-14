package com.example.restaurantapp.response.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String customerName;
    private String customerSurname;
    private String phoneNumber;
    private String address;
    private String status;
    private Timestamp createdTime;
    private List<FoodDetailResponse> foodDetailResponseList;
    //For restaurant information
    private Integer numberOfOrder;
    private String priority;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodDetailResponse {
        private String name;
        private Integer quantity;
        private BigDecimal price;
    }
}
