package com.example.restaurantapp.dataaccess;

import com.example.restaurantapp.dbmodel.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodItemsRepository extends JpaRepository<FoodItem, Long> {

}
