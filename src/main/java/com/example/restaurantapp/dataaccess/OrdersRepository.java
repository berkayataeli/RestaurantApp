package com.example.restaurantapp.dataaccess;

import com.example.restaurantapp.dbmodel.Orders;
import com.example.restaurantapp.dto.OrderCustomerDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Orders findTopByCustomerIdOrderByCreatedTimeDesc(Long customerId);

    @Query("SELECT new com.example.restaurantapp.dto.OrderCustomerDto(c.numberOfOrder, c.name, c.surname, c.phoneNumber, c.address, o.status) " +
            "FROM Orders o JOIN Customer c ON o.customerId = c.customerId " +
            "WHERE o.orderId = :orderId")
    OrderCustomerDto findOrderCustomerByOrderId(@Param("orderId") Long orderId);

}
