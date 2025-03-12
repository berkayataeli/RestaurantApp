package com.example.restaurantapp.dataaccess;

import com.example.restaurantapp.dbmodel.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    @Query("SELECT f FROM Food f JOIN MenuItem m ON f.foodId = m.foodId WHERE f.active = true AND m.menuId = :menuId ORDER BY f.type ASC, f.foodId ASC")
    List<Food> findActiveFoodsByMenuItemMenuIdOrderByTypeAscFoodIdAsc(@Param("menuId") Long menuId);

    @Modifying
    @Query("UPDATE Food f SET f.numberOfOrder = f.numberOfOrder + 1 WHERE f.foodId IN :foodIds")
    void incrementNumberOfOrderForFoods(@Param("foodIds") List<Long> foodIds);

}
