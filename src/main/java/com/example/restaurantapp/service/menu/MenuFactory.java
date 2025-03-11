package com.example.restaurantapp.service.menu;

import com.example.restaurantapp.dbmodel.FoodItem;
import com.example.restaurantapp.dbmodel.Menu;
import com.example.restaurantapp.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MenuFactory {

    private final MenuMapper menuMapper;

    public Menu createMenu(String dayOfWeek, List<FoodItem> foodItems) {
        return menuMapper.menuCreateRequestToMenuMapper(dayOfWeek, foodItems);
    }
}

