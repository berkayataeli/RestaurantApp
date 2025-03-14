package com.example.restaurantapp.response.order;

import com.example.restaurantapp.dto.FoodOrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdersByStatusResponse {

    private List<OrderDetails> orderResponsesByStatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetails {
        private Long orderId;
        private Timestamp createdTime;
        private Timestamp updatedTime;
        private List<FoodOrderItemDto> foodOrderItemDtos;
    }
}
