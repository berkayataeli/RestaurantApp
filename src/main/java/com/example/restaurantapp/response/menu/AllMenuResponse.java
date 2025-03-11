package com.example.restaurantapp.response.menu;

import com.example.restaurantapp.dbmodel.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllMenuResponse {

    private List<Menu> menuList;
}
