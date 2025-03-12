package com.example.restaurantapp.controller;

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

    @GetMapping("/listMenuByDay/{dayOfWeek}")
    public ResponseEntity<MenuResponse> listMenuByDay(@PathVariable @Pattern(regexp = "^(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)$",
            message = "Invalid dayOfWeek. Must be one of: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday")
                                                    String dayOfWeek) {
        return ResponseEntity.ok(menuService.listMenuByDay(dayOfWeek));
    }
}
