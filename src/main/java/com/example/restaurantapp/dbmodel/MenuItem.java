package com.example.restaurantapp.dbmodel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MENU_ITEM")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_ITEM_ID")
    private Long menuItemId;

    @Column(name = "MENU_ID", nullable = false)
    private Long menuId;

    @Column(name = "FOOD_ID", nullable = false)
    private Long foodId;

}
