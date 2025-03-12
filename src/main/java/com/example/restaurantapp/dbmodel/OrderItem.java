package com.example.restaurantapp.dbmodel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ORDER_ITEM")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long orderItemId;

    @Column(name = "ORDER_ID", nullable = false)
    private Long orderId;

    @Column(name = "FOOD_ID", nullable = false)
    private Long foodId;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

}
