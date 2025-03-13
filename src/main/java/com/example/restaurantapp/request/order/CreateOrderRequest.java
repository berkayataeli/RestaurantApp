package com.example.restaurantapp.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    private Long customerId;
    private List<OrderDetail> orderDetailList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetail {
        private Long foodId;
        private Integer quantity;
    }
}
