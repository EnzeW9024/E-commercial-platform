package com.ecommerce.controller;

import com.ecommerce.dto.OrderRequest;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.dto.OrderStatusUpdateRequest;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Order Controller
 * Handles order management REST API endpoints
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("DESC") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderResponse> orderPage;
        if (userId != null) {
            orderPage = orderService.getOrdersByUser(userId, pageable);
        } else if (status != null) {
            orderPage = orderService.getOrdersByStatus(status, pageable);
        } else {
            orderPage = orderService.getAllOrders(pageable);
        }

        return ResponseEntity.ok(orderPage.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.updateOrder(id, request);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse order = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable Long id) {
        OrderResponse order = orderService.cancelOrder(id);
        return ResponseEntity.ok(Map.of(
                "message", "Order cancelled successfully",
                "orderNumber", order.getOrderNumber(),
                "status", order.getStatus().name()
        ));
    }
}


