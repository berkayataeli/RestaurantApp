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

}
