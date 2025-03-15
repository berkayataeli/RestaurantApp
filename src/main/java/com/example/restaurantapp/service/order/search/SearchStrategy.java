package com.example.restaurantapp.service.order.search;

import com.example.restaurantapp.dbmodel.Orders;
import com.example.restaurantapp.request.order.OrderSearchRequest;
import org.springframework.data.jpa.domain.Specification;

public interface SearchStrategy {
    boolean isApplicable(OrderSearchRequest request);

    Specification<Orders> buildSpecification(OrderSearchRequest request);
}
