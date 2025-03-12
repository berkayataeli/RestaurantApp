package com.example.restaurantapp.controller;

import com.example.restaurantapp.exception.MenuNotFoundException;
import com.example.restaurantapp.response.menu.MenuResponse;
import com.example.restaurantapp.service.menu.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
public class MenuControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private MenuController menuController;

    @Mock
    private MenuService menuService;

    private MenuResponse dummyMenuResponse;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();

        // Create a mock MenuResponse for the tests
        MenuResponse.FoodDto foodDto = new MenuResponse.FoodDto();
        foodDto.setName("Soup");
        foodDto.setDescription("A tasty starter.");

        dummyMenuResponse = new MenuResponse();
        dummyMenuResponse.setDayOfWeek("Monday");
        dummyMenuResponse.setFoodList(List.of(foodDto));
    }

    @Test
    void testGetMenuByDay_ShouldReturnMenuResponse_WhenMenuExists() throws Exception {
        String dayOfWeek = "Monday";
        when(menuService.listMenuByDay(dayOfWeek)).thenReturn(dummyMenuResponse);

        mockMvc.perform(get("/api/menu/{dayOfWeek}", dayOfWeek)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.day").value("Monday"))
                .andExpect(jsonPath("$.foodList[0].name").value("Soup"));

        verify(menuService, times(1)).listMenuByDay(dayOfWeek);
    }

    @Test
    void testGetMenuByDay_ShouldReturn404_WhenMenuDoesNotExist() throws Exception {
        String dayOfWeek = "Friday";
        when(menuService.listMenuByDay(dayOfWeek)).thenThrow(new MenuNotFoundException("Menu for day '" + dayOfWeek + "' does not exist!"));

        mockMvc.perform(get("/api/menu/{dayOfWeek}", dayOfWeek)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Menu for day 'Monday' does not exist!"));

        verify(menuService, times(1)).listMenuByDay(dayOfWeek);
    }
}
