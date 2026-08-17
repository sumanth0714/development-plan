package com.food.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {

    private Long id;
    private String customerName;
    private Long restaurantId;
    private Double amount;
    private String status;
}