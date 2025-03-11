package com.example.restaurantapp.controller;

import com.example.restaurantapp.dbmodel.Menu;
import com.example.restaurantapp.exception.MenuNotFoundException;
import com.example.restaurantapp.response.menu.AllMenuResponse;
import com.example.restaurantapp.response.menu.MenuResponse;
import com.example.restaurantapp.service.menu.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class MenuControllerTest {

    @InjectMocks
    private MockMvc mockMvc;

    @Mock
    private MenuService menuService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MenuController(menuService)).build();
    }

    @Test
    void testGetMenuByDayOfWeek_MenuNotFound() throws Exception {
        String dayOfWeek = "Sunday";

        when(menuService.getMenu(dayOfWeek)).thenThrow(new MenuNotFoundException("Menu not found"));

        mockMvc.perform(get("/api/menu/getMenu/{dayOfWeek}", dayOfWeek)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Menu not found"));
    }


    @Test
    @DisplayName("Test getMenu() - Should return all menu items")
    void testGetMenu() throws Exception {
        List<Menu> menuList = new ArrayList<>();
        menuList.add(new Menu(1L, "Monday", new ArrayList<>()));
        AllMenuResponse allMenuResponse = new AllMenuResponse(menuList);

        when(menuService.getMenu()).thenReturn(allMenuResponse);

        mockMvc.perform(get("/api/menu/getMenu")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menuList").isArray())
                .andExpect(jsonPath("$.menuList[0].dayOfWeek").value("Monday"));

    }

    @Test
    @DisplayName("Test getMenu(dayOfWeek) - Should return menu for valid day of week")
    void testGetMenuByDayOfWeek_ValidDay() throws Exception {
        String dayOfWeek = "Monday";
        MenuResponse menuResponse = new MenuResponse(); // Mock a valid response object
        when(menuService.getMenu(dayOfWeek)).thenReturn(menuResponse);

        mockMvc.perform(get("/api/menu/getMenu/{dayOfWeek}", dayOfWeek)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test getMenu(dayOfWeek) - Should return 400 for invalid day of week")
    void testGetMenuByDayOfWeek_InvalidDay() throws Exception {
        String invalidDay = "Funday";

        mockMvc.perform(get("/api/menu/getMenu/{dayOfWeek}", invalidDay)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}