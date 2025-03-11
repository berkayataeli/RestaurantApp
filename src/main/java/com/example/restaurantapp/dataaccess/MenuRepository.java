package com.example.restaurantapp.dataaccess;

import com.example.restaurantapp.dbmodel.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    Boolean existsByDayOfWeek(String dayOfWeek);

    Optional<Menu> findByMenuId(Long menuId);

    Optional<Menu> findByDayOfWeek(String dayOfWeek);


}
