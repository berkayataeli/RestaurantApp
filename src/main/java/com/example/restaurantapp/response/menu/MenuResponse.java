package com.example.restaurantapp.response.menu;

import com.example.restaurantapp.dbmodel.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {

    private Menu menu;
}
