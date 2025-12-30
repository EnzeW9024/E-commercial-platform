package com.ecommerce.event;

import java.time.LocalDateTime;

/**
 * Order Status Changed Event
 * Published when order status is updated
 */
public class OrderStatusChangedEvent {
    
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime updatedAt;
    
    public OrderStatusChangedEvent() {
    }
    
    public OrderStatusChangedEvent(Long orderId, String orderNumber, Long userId,
                                  String oldStatus, String newStatus,
                                  LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getOldStatus() {
        return oldStatus;
    }
    
    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }
    
    public String getNewStatus() {
        return newStatus;
    }
    
    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

