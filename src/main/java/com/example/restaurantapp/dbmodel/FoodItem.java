package com.example.restaurantapp.dbmodel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "FOOD_ITEM")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem {

    @Id
    @Column(name = "FOOD_ID", nullable = false)
    private Long foodId;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "TYPE", nullable = false)
    private String type;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PRICE", nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "MENU_ID")
    private Menu menu;
}
