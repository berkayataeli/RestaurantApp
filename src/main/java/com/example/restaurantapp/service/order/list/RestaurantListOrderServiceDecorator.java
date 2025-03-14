package com.example.restaurantapp.service.order.list;

import com.example.restaurantapp.dataaccess.CustomerRepository;
import com.example.restaurantapp.enums.OrderPriorityEnum;
import com.example.restaurantapp.response.menu.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class RestaurantListOrderServiceDecorator extends ListOrderServiceDecorator {

    @Autowired
    private CustomerRepository customerRepository;

    private static final Long oneHour = 1000*60*60L;
    private static final Long halfHour = 1000*60*30L;

    public RestaurantListOrderServiceDecorator(CustomerListOrderService customerListOrderService) {
        super(customerListOrderService);
    }

    @Override
    public OrderResponse listOrdersByOrderId(Long orderId) {
        OrderResponse orderResponse = super.listOrdersByOrderId(orderId);

        //generate additional restaurant infos
        if (orderResponse.getStatus().equals("PREPARING")) {
            orderResponse.setPriority(generatePriority(orderResponse));
        }
        orderResponse.setNumberOfOrder(getCustomerNumberOfOrder(orderId));

        log.info("Restaurant Order returned {} for {}, ", orderResponse, orderId);
        return orderResponse;
    }

    private Integer getCustomerNumberOfOrder(Long orderId) {
        return customerRepository.findNumberOfOrdersByOrderId(orderId);
    }

    private String generatePriority(OrderResponse orderResponse) {
        long timeDifference = Math.abs(System.currentTimeMillis() - orderResponse.getCreatedTime().getTime());

        //If 1 hour has passed since the order was created -> high priority
        //Else If half an hour has passed -> medium priority
        //Otherwise -> low priority
        if(timeDifference > oneHour)
            return OrderPriorityEnum.HIGH.name();
        else if(timeDifference > halfHour)
            return OrderPriorityEnum.MEDIUM.name();
        else
            return OrderPriorityEnum.LOW.name();
    }

}
