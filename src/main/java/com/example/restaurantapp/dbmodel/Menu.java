package com.example.restaurantapp.dbmodel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MENU")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    @Id
    @Column(name = "MENU_ID", nullable = false)
    private Long menuId;

    @Column(name = "DAY_OF_WEEK", nullable = false)
    private String dayOfWeek;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FoodItem> foodItems = new ArrayList<>();
}
