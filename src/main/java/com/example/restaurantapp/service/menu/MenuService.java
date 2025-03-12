package com.example.restaurantapp.service.menu;

import com.example.restaurantapp.dataaccess.FoodRepository;
import com.example.restaurantapp.dataaccess.MenuRepository;
import com.example.restaurantapp.dbmodel.Food;
import com.example.restaurantapp.dbmodel.Menu;
import com.example.restaurantapp.exception.MenuNotFoundException;
import com.example.restaurantapp.mapper.MenuMapper;
import com.example.restaurantapp.response.menu.MenuResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final FoodRepository foodRepository;
    private final MenuMapper menuMapper;

    @Cacheable(value = "menuCache", key = "#dayOfWeek")
    public MenuResponse listMenuByDay(String dayOfWeek) {
        log.info("Menu requested for day: {}", dayOfWeek);
        Menu menu = menuRepository.findByDayOfWeek(dayOfWeek).orElseThrow(() -> new MenuNotFoundException("Menu for day '" + dayOfWeek + "' does not exist!"));

        List<Food> foodList = foodRepository.findFoodsByMenuItemMenuIdOrderByTypeAscFoodIdAsc(menu.getMenuId());

        // sorting by type and id
//        foodList.sort(Comparator.comparing(Food::getType)
//                .thenComparing(Food::getFoodId));

        List<MenuResponse.FoodDto> foodDtos = foodList.stream().map(menuMapper::foodToFoodDtoMapper).toList();
        MenuResponse menuResponse = menuMapper.menuCreateMapper(dayOfWeek, foodDtos);
        log.info("Menu returned by {} response {}, ", dayOfWeek, menuResponse);
        return menuResponse;
    }
}
