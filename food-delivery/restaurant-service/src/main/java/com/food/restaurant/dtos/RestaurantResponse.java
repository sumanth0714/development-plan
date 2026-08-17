package com.food.restaurant.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantResponse {

    private Long id;
    private String name;
    private String location;
    private Double rating;
}