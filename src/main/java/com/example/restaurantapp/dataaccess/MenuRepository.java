package com.example.restaurantapp.dataaccess;

import com.example.restaurantapp.dbmodel.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Optional<Menu> findByDayOfWeek(String dayOfWeek);

}
