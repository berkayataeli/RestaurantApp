package com.example.restaurantapp.service;

import com.example.restaurantapp.dataaccess.FoodRepository;
import com.example.restaurantapp.dataaccess.MenuRepository;
import com.example.restaurantapp.dbmodel.Food;
import com.example.restaurantapp.dbmodel.Menu;
import com.example.restaurantapp.exception.MenuNotFoundException;
import com.example.restaurantapp.mapper.MenuMapper;
import com.example.restaurantapp.response.menu.MenuResponse;
import com.example.restaurantapp.service.menu.MenuService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private MenuService menuService;

    @Test
    void listMenuByDay_ShouldThrowMenuNotFoundException_WhenMenuDoesNotExist() {
        String dayOfWeek = "Monday";
        when(menuRepository.findByDayOfWeek(dayOfWeek)).thenReturn(Optional.empty());

        assertThrows(MenuNotFoundException.class, () -> menuService.listMenuByDay(dayOfWeek));

        verifyNoInteractions(foodRepository);
        verifyNoInteractions(menuMapper);
    }

    @Test
    void testListMenuByDay_ShouldReturnMenuResponse_WhenDayIsValid() {
        String dayOfWeek = "Monday";

        Menu dummyMenu = new Menu();
        dummyMenu.setMenuId(1L);
        dummyMenu.setDayOfWeek(dayOfWeek);

        Food dummyFood = new Food();
        dummyFood.setFoodId(1L);
        dummyFood.setName("Arancini");
        dummyFood.setType("Starter");
        dummyFood.setActive(true);

        MenuResponse.FoodDto dummyFoodDto = new MenuResponse.FoodDto(1L, "Arancini", "Starter", null, null);
        MenuResponse dummyMenuResponse = new MenuResponse(dayOfWeek, List.of(dummyFoodDto));

        when(menuRepository.findByDayOfWeek(dayOfWeek)).thenReturn(Optional.of(dummyMenu));
        when(foodRepository.findActiveFoodsByMenuItemMenuIdOrderByTypeAscFoodIdAsc(1L)).thenReturn(List.of(dummyFood));
        when(menuMapper.foodToFoodDtoMapper(dummyFood)).thenReturn(dummyFoodDto);
        when(menuMapper.menuCreateMapper(dayOfWeek, List.of(dummyFoodDto))).thenReturn(dummyMenuResponse);

        MenuResponse result = menuService.listMenuByDay(dayOfWeek);

        assertNotNull(result);
        assertEquals(dayOfWeek, result.getDayOfWeek());
        assertEquals(1, result.getFoodList().size());
        assertEquals("Arancini", result.getFoodList().get(0).getName());

        verify(menuRepository, times(1)).findByDayOfWeek(dayOfWeek);
        verify(foodRepository, times(1)).findActiveFoodsByMenuItemMenuIdOrderByTypeAscFoodIdAsc(1L);
    }

}
