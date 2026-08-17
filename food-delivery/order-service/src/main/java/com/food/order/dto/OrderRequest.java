package com.food.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderRequest {

    @NotBlank
    private String customerName;

    @NotNull
    private Long restaurantId;

    @NotNull
    @Positive
    private Double amount;
}