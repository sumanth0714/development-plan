package com.food.restaurant.service;

import com.food.restaurant.dtos.RestaurantRequest;
import com.food.restaurant.dtos.RestaurantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantService {

    RestaurantResponse create(RestaurantRequest request);

    RestaurantResponse getById(Long id);

    Page<RestaurantResponse> getAll(Pageable pageable);

    RestaurantResponse update(Long id, RestaurantRequest request);

    void delete(Long id);
}