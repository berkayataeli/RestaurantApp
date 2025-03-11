package com.example.restaurantapp.controller;

import com.example.restaurantapp.request.menu.CreateMenuRequest;
import com.example.restaurantapp.request.menu.InsertMenuRequest;
import com.example.restaurantapp.request.menu.UpdateMenuRequest;
import com.example.restaurantapp.response.menu.AllMenuResponse;
import com.example.restaurantapp.response.menu.MenuResponse;
import com.example.restaurantapp.service.menu.MenuService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/getMenu")
    public ResponseEntity<AllMenuResponse> getMenu() {
        return ResponseEntity.ok(menuService.getMenu());
    }

    @GetMapping("/getMenu/{dayOfWeek}")
    public ResponseEntity<MenuResponse> getMenu(@PathVariable @Pattern(regexp = "^(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)$",
            message = "Invalid dayOfWeek. Must be one of: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday")
                                                    String dayOfWeek) {
        return ResponseEntity.ok(menuService.getMenu(dayOfWeek));
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createMenu(@RequestBody CreateMenuRequest createMenuRequest) {
        menuService.createMenu(createMenuRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateMenu(@RequestBody UpdateMenuRequest updateMenuRequest) {
        menuService.updateMenu(updateMenuRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/insert")
    public ResponseEntity<Void> insertMenu(@RequestBody InsertMenuRequest insertMenuRequest) {
        menuService.insertMenu(insertMenuRequest);
        return ResponseEntity.ok().build();
    }
}
