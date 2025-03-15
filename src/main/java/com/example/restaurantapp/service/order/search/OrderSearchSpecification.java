package com.example.restaurantapp.service.order.search;

import com.example.restaurantapp.dbmodel.Orders;
import com.example.restaurantapp.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

public class OrderSearchSpecification {

    public static Specification<Orders> withCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("customerId"), customerId);
    }

    public static Specification<Orders> withStatus(String status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), OrderStatus.valueOf(status).getValue());
    }

}
