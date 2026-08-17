package com.food.order.dto;

import lombok.Data;

@Data
public class RestaurantResponse {

    private Long id;
    private String name;
    private String location;
    private Double rating;
}