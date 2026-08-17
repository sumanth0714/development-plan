package com.food.restaurant.service;

import com.food.restaurant.dtos.RestaurantRequest;
import com.food.restaurant.dtos.RestaurantResponse;
import com.food.restaurant.model.Restaurant;
import com.food.restaurant.repo.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public RestaurantResponse create(RestaurantRequest request) {

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .location(request.getLocation())
                .rating(request.getRating())
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);

        return mapToResponse(saved);
    }

    @Override
    public RestaurantResponse getById(Long id) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Restaurant not found: " + id));

        return mapToResponse(restaurant);
    }

    @Override
    public Page<RestaurantResponse> getAll(Pageable pageable) {

        return restaurantRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public RestaurantResponse update(
            Long id,
            RestaurantRequest request) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Restaurant not found: " + id));

        restaurant.setName(request.getName());
        restaurant.setLocation(request.getLocation());
        restaurant.setRating(request.getRating());

        Restaurant updated = restaurantRepository.save(restaurant);

        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {

        if (!restaurantRepository.existsById(id)) {
            throw new RuntimeException(
                    "Restaurant not found: " + id);
        }

        restaurantRepository.deleteById(id);
    }

    private RestaurantResponse mapToResponse(Restaurant restaurant) {

        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .location(restaurant.getLocation())
                .rating(restaurant.getRating())
                .build();
    }
}