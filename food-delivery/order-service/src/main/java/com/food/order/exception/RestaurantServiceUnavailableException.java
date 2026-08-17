package com.food.order.exception;

public class RestaurantServiceUnavailableException
        extends RuntimeException {

    public RestaurantServiceUnavailableException(
            String message) {

        super(message);
    }
}