package com.example.restaurantapp.mapper;

import com.example.restaurantapp.dbmodel.FoodItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.example.restaurantapp.dbmodel.Menu;
import com.example.restaurantapp.request.menu.CreateMenuRequest;

import java.util.List;

import static com.example.restaurantapp.util.ApplicationConstants.MAPPER_COMPONENT_MODEL;

@Mapper(componentModel = MAPPER_COMPONENT_MODEL, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenuMapper {

    @Mapping(target = "dayOfWeek", source = "dayOfWeek")
    @Mapping(target = "foodItems", source = "foodItems")
    Menu menuCreateRequestToMenuMapper(String dayOfWeek, List<FoodItem> foodItems);

    List<FoodItem> foodItemListMapper(List<CreateMenuRequest.FoodItemRequest> foodItemRequestList);
}
