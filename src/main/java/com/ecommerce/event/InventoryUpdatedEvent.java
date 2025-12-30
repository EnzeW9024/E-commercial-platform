package com.ecommerce.event;

import java.time.LocalDateTime;

/**
 * Inventory Updated Event
 * Published when product inventory is updated
 */
public class InventoryUpdatedEvent {
    
    private Long productId;
    private String productName;
    private Integer oldStock;
    private Integer newStock;
    private Integer quantityChanged;
    private String reason; // ORDER_CREATED, ORDER_CANCELLED, MANUAL_UPDATE, etc.
    private Long orderId; // Optional: related order ID
    private LocalDateTime updatedAt;
    
    public InventoryUpdatedEvent() {
    }
    
    public InventoryUpdatedEvent(Long productId, String productName, Integer oldStock,
                                Integer newStock, Integer quantityChanged, String reason,
                                Long orderId, LocalDateTime updatedAt) {
        this.productId = productId;
        this.productName = productName;
        this.oldStock = oldStock;
        this.newStock = newStock;
        this.quantityChanged = quantityChanged;
        this.reason = reason;
        this.orderId = orderId;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Integer getOldStock() {
        return oldStock;
    }
    
    public void setOldStock(Integer oldStock) {
        this.oldStock = oldStock;
    }
    
    public Integer getNewStock() {
        return newStock;
    }
    
    public void setNewStock(Integer newStock) {
        this.newStock = newStock;
    }
    
    public Integer getQuantityChanged() {
        return quantityChanged;
    }
    
    public void setQuantityChanged(Integer quantityChanged) {
        this.quantityChanged = quantityChanged;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

