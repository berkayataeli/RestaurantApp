package com.example.restaurantapp.service.menu;

import com.example.restaurantapp.dbmodel.Menu;
import com.example.restaurantapp.mapper.MenuMapper;
import com.example.restaurantapp.request.menu.CreateMenuRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class MenuFactory {

    private final MenuMapper menuMapper;

    private Menu createMenu(CreateMenuRequest.MenuItem menuItem) {
        return menuMapper.menuCreateRequestToMenuMapper(menuItem.getDayOfWeek(), menuMapper.foodItemListMapper(menuItem.getFoodItemRequestList()));
    }


    public List<Menu> createMenus(List<CreateMenuRequest.MenuItem> menuItems) {
        return menuItems.stream()
                .map(this::createMenu)
                .collect(Collectors.toList());

    }
}

