package com.food.restaurant.controller;

import com.food.restaurant.dtos.RestaurantRequest;
import com.food.restaurant.dtos.RestaurantResponse;
import com.food.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse create(
            @Valid @RequestBody RestaurantRequest request) {

        return restaurantService.create(request);
    }

    @GetMapping("/{id}")
    public RestaurantResponse getById(
            @PathVariable Long id) {

        return restaurantService.getById(id);
    }

    @GetMapping
    public Page<RestaurantResponse> getAll(
            Pageable pageable) {

        return restaurantService.getAll(pageable);
    }

    @PutMapping("/{id}")
    public RestaurantResponse update(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequest request) {

        return restaurantService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        restaurantService.delete(id);
    }
}