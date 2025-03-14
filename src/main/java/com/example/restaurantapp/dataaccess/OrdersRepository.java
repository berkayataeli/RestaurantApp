package com.example.restaurantapp.dataaccess;

import com.example.restaurantapp.dbmodel.Orders;
import com.example.restaurantapp.dto.CustomersOrderDto;
import com.example.restaurantapp.dto.OrderCustomerDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Orders findTopByCustomerIdOrderByCreatedTimeDesc(Long customerId);

    @Query("SELECT new com.example.restaurantapp.dto.OrderCustomerDto(c.name, c.surname, c.phoneNumber, c.address, o.status, o.createdTime) " +
            "FROM Orders o JOIN Customer c ON o.customerId = c.customerId " +
            "WHERE o.orderId = :orderId")
    Optional<OrderCustomerDto> findOrderCustomerByOrderId(@Param("orderId") Long orderId);

    List<Orders> findAllByStatusOrderByCreatedTimeDesc(Integer status);

    List<Orders> findAllByCustomerId(Long customerId);

    @Query("SELECT new com.example.restaurantapp.dto.CustomersOrderDto(o.orderId, o.status, c.address, c.phoneNumber) " +
            "FROM Orders o JOIN Customer c ON o.customerId = c.customerId " +
            "WHERE o.orderId IN :orderIds ORDER BY o.createdTime DESC")
    List<CustomersOrderDto> findOrderCustomersByOrderIdInOrderByCreatedTime(@Param("orderIds") List<Long> orderIds);

}
