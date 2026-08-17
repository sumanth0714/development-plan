package com.food.order.service;

import com.food.order.client.RestaurantClient;
import com.food.order.dto.OrderRequest;
import com.food.order.dto.OrderResponse;
import com.food.order.dto.RestaurantResponse;
import com.food.order.exception.RestaurantServiceUnavailableException;
import com.food.order.model.Order;
import com.food.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantClient restaurantClient;

    @Override
    public OrderResponse create(OrderRequest request) {

        // Call Restaurant Service
        restaurantClient.getRestaurant(
                request.getRestaurantId());

        Order order = Order.builder()
                .customerName(request.getCustomerName())
                .restaurantId(request.getRestaurantId())
                .amount(request.getAmount())
                .status("CREATED")
                .build();

        Order saved = orderRepository.save(order);

        return mapToResponse(saved);
    }

    @Override
    public OrderResponse getById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found: " + id));

        return mapToResponse(order);
    }

    @Override
    public Page<OrderResponse> getAll(Pageable pageable) {

        return orderRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    @CircuitBreaker(
            name = "restaurantService",
            fallbackMethod = "restaurantFallback"
    )
    private RestaurantResponse getRestaurant(Long restaurantId) {

        return restaurantClient.getRestaurant(restaurantId);
    }

    private RestaurantResponse restaurantFallback(
            Long restaurantId,
            Throwable throwable) {

        throw new RestaurantServiceUnavailableException(
                "Restaurant service is currently unavailable. "
                        + "Please try again later.");
    }

    private OrderResponse mapToResponse(Order order) {

        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .restaurantId(order.getRestaurantId())
                .amount(order.getAmount())
                .status(order.getStatus())
                .build();
    }
}