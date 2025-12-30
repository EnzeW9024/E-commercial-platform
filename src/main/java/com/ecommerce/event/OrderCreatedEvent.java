package com.ecommerce.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Created Event
 * Published when a new order is created
 */
public class OrderCreatedEvent {
    
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private BigDecimal totalAmount;
    private List<OrderItemEvent> items;
    private String shippingAddress;
    private LocalDateTime createdAt;
    
    public OrderCreatedEvent() {
    }
    
    public OrderCreatedEvent(Long orderId, String orderNumber, Long userId, 
                            BigDecimal totalAmount, List<OrderItemEvent> items,
                            String shippingAddress, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
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
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public List<OrderItemEvent> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemEvent> items) {
        this.items = items;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Order Item Event (nested class for order items)
     */
    public static class OrderItemEvent {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
        
        public OrderItemEvent() {
        }
        
        public OrderItemEvent(Long productId, String productName, Integer quantity,
                             BigDecimal unitPrice, BigDecimal subtotal) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
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
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        
        public BigDecimal getUnitPrice() {
            return unitPrice;
        }
        
        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }
        
        public BigDecimal getSubtotal() {
            return subtotal;
        }
        
        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }
    }
}

