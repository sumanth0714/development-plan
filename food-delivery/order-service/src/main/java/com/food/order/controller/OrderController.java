package com.food.order.controller;

import com.food.order.dto.OrderRequest;
import com.food.order.dto.OrderResponse;
import com.food.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(
            @Valid @RequestBody OrderRequest request) {

        return orderService.create(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(
            @PathVariable Long id) {

        return orderService.getById(id);
    }

    @GetMapping
    public Page<OrderResponse> getAll(
            Pageable pageable) {

        return orderService.getAll(pageable);
    }
}