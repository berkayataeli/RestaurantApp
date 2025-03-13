package com.example.restaurantapp.controller;

import com.example.restaurantapp.dataaccess.FoodRepository;
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
    private MenuController menuControllerTest;

    @Mock
    private MenuService menuService;

    @Mock
    private FoodRepository foodRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(menuControllerTest).build();
    }

    @Test
    void testGetMenuByDay_ShouldReturnMenuResponse_WhenMenuExists() throws Exception {
        String dayOfWeek = "Monday";
        when(menuService.listMenuByDay(dayOfWeek)).thenReturn(getDummyMenuResponse());

        mockMvc.perform(get("/api/menu/listMenuByDay/{dayOfWeek}", dayOfWeek)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private MenuResponse getDummyMenuResponse() {
        MenuResponse.FoodDto foodDto = new MenuResponse.FoodDto();
        foodDto.setName("Arancini");
        foodDto.setType("Starter");

        MenuResponse dummyMenuResponse = new MenuResponse();
        dummyMenuResponse.setDayOfWeek("Monday");
        dummyMenuResponse.setFoodList(List.of(foodDto));

        return dummyMenuResponse;
    }

    @Test
    void testGetMenuByDay_ShouldReturn500_WhenMenuDoesNotExist() throws Exception {
        String dayOfWeek = "Friday";
        when(menuService.listMenuByDay(dayOfWeek)).thenThrow(new MenuNotFoundException(dayOfWeek));

        mockMvc.perform(get("/api/menu/listMenuByDay/{dayOfWeek}", dayOfWeek)
                        .contentType(MediaType.APPLICATION_JSON));

        verifyNoInteractions(foodRepository);
    }

    @Test
    void listMenuByDay_ShouldReturnBadRequest_WhenDayOfWeekIsInvalid() throws Exception {
        String invalidDayOfWeek = "InvalidDay";

        mockMvc.perform(get("/api/menu/listMenuByDay/{dayOfWeek}", invalidDayOfWeek)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(menuService);
    }
}
