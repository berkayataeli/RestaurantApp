package com.example.restaurantapp.dataaccess;

import com.example.restaurantapp.dbmodel.OrderItem;
import com.example.restaurantapp.dto.FoodOrderItemDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT new com.example.restaurantapp.dto.FoodOrderItemDto(f.name, f.price, oi.quantity) " +
            "FROM Food f JOIN OrderItem oi ON f.foodId = oi.foodId " +
            "WHERE oi.orderId = :orderId")
    List<FoodOrderItemDto> findFoodOrderItemsByOrderId(@Param("orderId") Long orderId);

    List<OrderItem> findAllByOrderId(Long orderId);
}
