package com.example.restaurantapp.mapper;

import com.example.restaurantapp.dbmodel.OrderItem;
import com.example.restaurantapp.dbmodel.Orders;
import com.example.restaurantapp.dto.FoodOrderItemDto;
import com.example.restaurantapp.dto.OrderCustomerDto;
import com.example.restaurantapp.enums.OrderStatus;
import com.example.restaurantapp.request.order.CreateOrderRequest;
import com.example.restaurantapp.response.menu.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.sql.Timestamp;
import java.util.List;

import static com.example.restaurantapp.util.ApplicationConstants.MAPPER_COMPONENT_MODEL;

@Mapper(componentModel = MAPPER_COMPONENT_MODEL, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "status", expression = "java(getStatus())")
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "createdTime", expression = "java(getCreatedTime())")
    Orders createOrdersMapper(Long customerId);

    default Integer getStatus() {
        return OrderStatus.PREPARING.getValue();
    }

    default Timestamp getCreatedTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "foodId", expression = "java(orderDetail.getFoodId())")
    @Mapping(target = "quantity", expression = "java(orderDetail.getQuantity())")
    OrderItem createOrderItemMapper(Long orderId, CreateOrderRequest.OrderDetail orderDetail);

    @Mapping(target = "status", expression = "java(getOrderResponseStatus(orderCustomerDto.getStatus()))")
    @Mapping(target = "foodDetailResponseList", source = "foodDetailResponseList")
    OrderResponse orderResponseMapper(OrderCustomerDto orderCustomerDto, List<OrderResponse.FoodDetailResponse> foodDetailResponseList);

    default String getOrderResponseStatus(Integer status) {
        if (OrderStatus.PREPARING.getValue() == status) {
            return OrderStatus.PREPARING.name();
        } else if (OrderStatus.DELIVERING.getValue() == status) {
            return OrderStatus.DELIVERING.name();
        } else if (OrderStatus.DELIVERED.getValue() == status) {
            return OrderStatus.DELIVERED.name();
        } else {
            return OrderStatus.CANCELLED.name();
        }
    }

    OrderResponse.FoodDetailResponse foodDetailResponseMapper(FoodOrderItemDto foodOrderItemDto);
}
