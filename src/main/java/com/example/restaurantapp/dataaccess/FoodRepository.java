package com.example.restaurantapp.dataaccess;

import com.example.restaurantapp.dbmodel.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {

    @Query("SELECT f FROM Food f JOIN MenuItem m ON f.foodId = m.foodId WHERE m.menuId = :menuId ORDER BY f.type ASC, f.foodId ASC")
    List<Food> findFoodsByMenuItemMenuIdOrderByTypeAscFoodIdAsc(@Param("menuId") Long menuId);

}
