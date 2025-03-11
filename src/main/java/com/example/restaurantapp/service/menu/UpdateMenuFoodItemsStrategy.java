package com.example.restaurantapp.service.menu;

import com.example.restaurantapp.dbmodel.Menu;
import com.example.restaurantapp.request.menu.UpdateMenuRequest;

public class UpdateMenuFoodItemsStrategy implements UpdateMenuStrategy {

    @Override
    public void update(Menu menu, UpdateMenuRequest updateMenuRequest) {
        menu.setFoodItems(updateMenuRequest.getMenu().getFoodItems());
    }
}
