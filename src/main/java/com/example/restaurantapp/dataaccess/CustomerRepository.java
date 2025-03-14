package com.example.restaurantapp.dataaccess;

import com.example.restaurantapp.dbmodel.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Modifying
    @Query(value = "UPDATE Customer SET numberOfOrder = numberOfOrder + 1 WHERE customerId = :customerId")
    void incrementNumberOfOrderNative(@Param("customerId") Long customerId);

    @Query("SELECT c.numberOfOrder FROM Customer c JOIN Orders o ON c.customerId = o.customerId WHERE o.orderId = :orderId")
    Integer findNumberOfOrdersByOrderId(@Param("orderId") Long orderId);

}
