package com.ecommerce.dto;

import com.ecommerce.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Order Status Update Request DTO
 */
public class OrderStatusUpdateRequest {

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}



