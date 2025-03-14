package com.example.restaurantapp.response.order;

import com.example.restaurantapp.dto.FoodOrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrdersResponse {

    private List<CustomerOrderDetail> customerOrderDetails;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerOrderDetail {
        private Long orderId;
        private String orderStatus;
        private String address;
        private String phoneNumber;
        private List<FoodOrderItemDto> foodOrderItemDtos;
    }
}
