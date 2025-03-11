package com.example.restaurantapp.service.menu;

import com.example.restaurantapp.dbmodel.Menu;
import com.example.restaurantapp.request.menu.UpdateMenuRequest;

public interface UpdateMenuStrategy {
    void update(Menu menu, UpdateMenuRequest updateMenuRequest);
}
