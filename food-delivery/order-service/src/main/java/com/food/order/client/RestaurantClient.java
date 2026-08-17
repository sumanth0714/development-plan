package com.food.order.client;

import com.food.order.dto.RestaurantResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "restaurant-service")
public interface RestaurantClient {

    @GetMapping("/restaurants/{id}")
    RestaurantResponse getRestaurant(
            @PathVariable Long id);
}