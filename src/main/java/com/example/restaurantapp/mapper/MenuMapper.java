package com.example.restaurantapp.mapper;

import com.example.restaurantapp.dbmodel.Food;
import com.example.restaurantapp.response.menu.MenuResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

import static com.example.restaurantapp.util.ApplicationConstants.MAPPER_COMPONENT_MODEL;

@Mapper(componentModel = MAPPER_COMPONENT_MODEL, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenuMapper {

    @Mapping(target = "dayOfWeek", source = "dayOfWeek")
    @Mapping(target = "foodList", source = "foodDtoList")
    MenuResponse menuCreateMapper(String dayOfWeek, List<MenuResponse.FoodDto> foodDtoList);


    MenuResponse.FoodDto foodToFoodDtoMapper(Food food);
}
