package com.food.order.service;

import com.food.order.dto.OrderRequest;
import com.food.order.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse create(OrderRequest request);

    OrderResponse getById(Long id);

    Page<OrderResponse> getAll(Pageable pageable);
}