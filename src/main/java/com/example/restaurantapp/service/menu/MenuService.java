package com.example.restaurantapp.service.menu;

import com.example.restaurantapp.dataaccess.MenuRepository;
import com.example.restaurantapp.dbmodel.FoodItem;
import com.example.restaurantapp.dbmodel.Menu;
import com.example.restaurantapp.exception.MenuAlreadyExistsException;
import com.example.restaurantapp.exception.MenuNotFoundException;
import com.example.restaurantapp.mapper.MenuMapper;
import com.example.restaurantapp.request.menu.CreateMenuRequest;
import com.example.restaurantapp.request.menu.UpdateMenuRequest;
import com.example.restaurantapp.response.menu.AllMenuResponse;
import com.example.restaurantapp.response.menu.MenuResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuFactory menuFactory;
    private final MenuMapper menuMapper;

    @Cacheable(value = "menuCache", key = "'allMenus'")
    public AllMenuResponse getMenu() {
        log.info("All menus requested");
        return new AllMenuResponse(menuRepository.findAll().stream().sorted(Comparator.comparing(Menu::getMenuId)).collect(Collectors.toList()));
    }

    @Cacheable(value = "menuCache", key = "#dayOfWeek")
    public MenuResponse getMenu(String dayOfWeek) {
        log.info("Menu requested for day: {}", dayOfWeek);
        Menu menu = menuRepository.findByDayOfWeek(dayOfWeek).orElseThrow(() -> new MenuNotFoundException("Menu for day '" + dayOfWeek + "' does not exist!"));

        // sorting by type and id
        List<FoodItem> foodItems = menu.getFoodItems();
        foodItems.sort(Comparator.comparing(FoodItem::getType)
                .thenComparing(FoodItem::getFoodId));
        menu.setFoodItems(foodItems);

        log.info("Menu returned response {}, ", dayOfWeek);
        return new MenuResponse(menu);
    }

    @Transactional
    @CacheEvict(value = "menuCache", allEntries = true)
    public void createMenu(CreateMenuRequest createMenuRequest) {
        log.info("Create menu service request : {},", createMenuRequest);

        // If there is a menu for the same day, throw "MenuAlreadyExistsException"
        if (menuRepository.findByDayOfWeek(createMenuRequest.getDayOfWeek()).isPresent()) {
            throw new MenuAlreadyExistsException("Menu for day '" + createMenuRequest.getDayOfWeek() + "' already exists!");
        }

        // Create menu
        Menu menu = menuFactory.createMenu(
                createMenuRequest.getDayOfWeek(),
                menuMapper.foodItemListMapper(createMenuRequest.getFoodItemRequestList())
        );

        menuRepository.save(menu);
        log.info("Menu created");
    }

    @Transactional
    @CacheEvict(value = "menuCache", key = "#updateMenuRequest.getMenu().getDayOfWeek()")
    public void updateMenu(UpdateMenuRequest updateMenuRequest) {
        log.info("Update menu service request : {},", updateMenuRequest.getMenu().getDayOfWeek());

        Menu menu = menuRepository.findByDayOfWeek(updateMenuRequest.getMenu().getDayOfWeek())
                .orElseThrow(() -> new MenuNotFoundException("Menu for day '" + updateMenuRequest.getMenu().getDayOfWeek() + "' not found!"));

        // Update menu
        UpdateMenuStrategy updateMenuStrategy = determineStrategy(updateMenuRequest);
        updateMenuStrategy.update(menu, updateMenuRequest);

        menuRepository.save(updateMenuRequest.getMenu());
        log.info("Menu updated");
    }

    // Determining which part of the menu to update
    private UpdateMenuStrategy determineStrategy(UpdateMenuRequest updateMenuRequest) {
        if (updateMenuRequest.getMenu().getFoodItems() != null && updateMenuRequest.getMenu().getDayOfWeek() != null) {
            return new UpdateMenuFullStrategy();
        } else if (updateMenuRequest.getMenu().getFoodItems() != null) {
            return new UpdateMenuFoodItemsStrategy();
        } else if (updateMenuRequest.getMenu().getDayOfWeek() != null) {
            return new UpdateMenuDayOfWeekStrategy();
        } else {
            throw new IllegalArgumentException("Invalid UpdateMenuRequest: No fields to update");
        }
    }

}
