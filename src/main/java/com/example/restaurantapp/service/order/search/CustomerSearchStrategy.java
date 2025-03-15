package com.example.restaurantapp.service.order.search;

import com.example.restaurantapp.dbmodel.Orders;
import com.example.restaurantapp.request.order.OrderSearchRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CustomerSearchStrategy implements SearchStrategy {

    @Override
    public boolean isApplicable(OrderSearchRequest request) {
        return request.getCustomerId() != null && request.getStatus() == null;
    }

    @Override
    public Specification<Orders> buildSpecification(OrderSearchRequest request) {
        return (root, query, cb) -> cb.equal(root.get("customerId"), request.getCustomerId());
    }
}
