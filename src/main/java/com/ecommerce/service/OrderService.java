package com.ecommerce.service;

import com.ecommerce.dto.OrderItemRequest;
import com.ecommerce.dto.OrderItemResponse;
import com.ecommerce.dto.OrderRequest;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.dto.OrderStatusUpdateRequest;
import com.ecommerce.event.InventoryUpdatedEvent;
import com.ecommerce.event.OrderCreatedEvent;
import com.ecommerce.event.OrderStatusChangedEvent;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Order Service
 * Business logic for order management with Redis caching
 */
@Service
@Transactional
@SuppressWarnings("null")
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private KafkaProducerService kafkaProducerService;

    private OrderResponse convertToResponse(Order order) {
        Objects.requireNonNull(order, "order must not be null");
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUser().getId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setTotalItems(order.getTotalItems());
        response.setShippingAddress(order.getShippingAddress());
        response.setBillingAddress(order.getBillingAddress());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setNotes(order.getNotes());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> {
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    itemResponse.setId(item.getId());
                    itemResponse.setProductId(item.getProduct().getId());
                    itemResponse.setProductName(item.getProductName());
                    itemResponse.setUnitPrice(item.getProductPrice());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setSubtotal(item.getSubtotal());
                    return itemResponse;
                })
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        Objects.requireNonNull(pageable, "pageable must not be null");
        return orderRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    /**
     * Get orders by user ID
     * Cached for 10 minutes
     */
    @Cacheable(value = "orders", key = "'user:' + #userId")
    public List<OrderResponse> getOrdersByUser(Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        return orderRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Page<OrderResponse> getOrdersByUser(Long userId, Pageable pageable) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(pageable, "pageable must not be null");
        return orderRepository.findByUserId(userId, pageable)
                .map(this::convertToResponse);
    }

    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return orderRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(pageable, "pageable must not be null");
        return orderRepository.findByStatus(status, pageable)
                .map(this::convertToResponse);
    }

    /**
     * Get order by ID
     * Cached for 5 minutes
     */
    @Cacheable(value = "order", key = "#id")
    public OrderResponse getOrderById(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return convertToResponse(order);
    }

    /**
     * Create a new order
     * Evicts orders cache and product cache (stock updated)
     */
    @Caching(evict = {
        @CacheEvict(value = "orders", allEntries = true),
        @CacheEvict(value = "product", allEntries = true) // Product stock changed
    })
    public OrderResponse createOrder(OrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalItems = 0;
        // Track old stock values before modification for Kafka events
        java.util.Map<Long, Integer> oldStockMap = new java.util.HashMap<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Track old stock before modification
            oldStockMap.put(product.getId(), product.getStock());
            
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);
            totalItems += itemRequest.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        order.setTotalItems(totalItems);

        orderItems.forEach(order::addItem);

        Order savedOrder = orderRepository.save(order);
        OrderResponse response = convertToResponse(savedOrder);
        
        // Send Kafka events
        sendOrderCreatedEvent(savedOrder);
        sendInventoryUpdatedEvents(savedOrder, "ORDER_CREATED", oldStockMap);
        
        return response;
    }

    /**
     * Update order status
     * Evicts order and orders cache
     */
    @Caching(evict = {
        @CacheEvict(value = "order", key = "#orderId"),
        @CacheEvict(value = "orders", allEntries = true)
    })
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(request.getStatus());

        if (request.getStatus() == OrderStatus.CANCELLED && previousStatus != OrderStatus.CANCELLED) {
            // Track old stock before restoring inventory
            java.util.Map<Long, Integer> oldStockMap = new java.util.HashMap<>();
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                oldStockMap.put(product.getId(), product.getStock());
            }
            
            restoreInventory(order);
            order.setPaymentStatus("REFUNDED");
            sendInventoryUpdatedEvents(order, "ORDER_CANCELLED", oldStockMap);
        }

        Order savedOrder = orderRepository.save(order);
        
        // Send Kafka event for status change
        sendOrderStatusChangedEvent(savedOrder, previousStatus);
        
        return convertToResponse(savedOrder);
    }

    /**
     * Update order
     * Evicts order and orders cache, and product cache (stock updated)
     */
    @Caching(evict = {
        @CacheEvict(value = "order", key = "#orderId"),
        @CacheEvict(value = "orders", allEntries = true),
        @CacheEvict(value = "product", allEntries = true) // Product stock changed
    })
    public OrderResponse updateOrder(Long orderId, OrderRequest request) {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot update a cancelled order");
        }

        // Track old items' stock and quantities before restoring inventory (for Kafka events)
        java.util.Map<Long, Integer> oldStockBeforeRestore = new java.util.HashMap<>();
        java.util.Map<Long, Integer> restoredQuantities = new java.util.HashMap<>();
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            oldStockBeforeRestore.put(product.getId(), product.getStock());
            restoredQuantities.put(product.getId(), item.getQuantity());
        }
        
        // Restore inventory for existing items
        restoreInventory(order);
        order.getItems().clear();

        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNotes(request.getNotes());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalItems = 0;
        // Track old stock values before deducting new items (for Kafka events)
        java.util.Map<Long, Integer> oldStockBeforeDeduction = new java.util.HashMap<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Track old stock before modification
            oldStockBeforeDeduction.put(product.getId(), product.getStock());
            
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);
            totalItems += itemRequest.getQuantity();
        }

        orderItems.forEach(order::addItem);
        order.setTotalAmount(totalAmount);
        order.setTotalItems(totalItems);

        Order savedOrder = orderRepository.save(order);
        
        // Send Kafka events for inventory updates
        // First, send events for restored items (if any were removed)
        if (!oldStockBeforeRestore.isEmpty()) {
            sendInventoryUpdatedEventsForRestoredItems(order, oldStockBeforeRestore, restoredQuantities);
        }
        // Then, send events for newly deducted items
        sendInventoryUpdatedEvents(savedOrder, "ORDER_UPDATED", oldStockBeforeDeduction);
        
        return convertToResponse(savedOrder);
    }

    /**
     * Delete order
     * Evicts order and orders cache, and product cache (stock restored)
     */
    @Caching(evict = {
        @CacheEvict(value = "order", key = "#orderId"),
        @CacheEvict(value = "orders", allEntries = true),
        @CacheEvict(value = "product", allEntries = true) // Product stock restored
    })
    public void deleteOrder(Long orderId) {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        restoreInventory(order);
        orderRepository.delete(order);
    }

    /**
     * Cancel order
     * Evicts order and orders cache, and product cache (stock restored)
     */
    @Caching(evict = {
        @CacheEvict(value = "order", key = "#orderId"),
        @CacheEvict(value = "orders", allEntries = true),
        @CacheEvict(value = "product", allEntries = true) // Product stock restored
    })
    public OrderResponse cancelOrder(Long orderId) {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return convertToResponse(order);
        }
        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus("REFUNDED");
        
        // Track old stock before restoring inventory
        java.util.Map<Long, Integer> oldStockMap = new java.util.HashMap<>();
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            oldStockMap.put(product.getId(), product.getStock());
        }
        
        restoreInventory(order);
        
        Order savedOrder = orderRepository.save(order);
        
        // Send Kafka events
        sendOrderStatusChangedEvent(savedOrder, previousStatus);
        sendInventoryUpdatedEvents(savedOrder, "ORDER_CANCELLED", oldStockMap);
        
        return convertToResponse(savedOrder);
    }

    private void restoreInventory(Order order) {
        Objects.requireNonNull(order, "order must not be null");
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
    }
    
    /**
     * Send order created event to Kafka
     */
    private void sendOrderCreatedEvent(Order order) {
        try {
            List<OrderCreatedEvent.OrderItemEvent> itemEvents = order.getItems().stream()
                    .map(item -> new OrderCreatedEvent.OrderItemEvent(
                            item.getProduct().getId(),
                            item.getProductName(),
                            item.getQuantity(),
                            item.getProductPrice(),
                            item.getSubtotal()
                    ))
                    .collect(Collectors.toList());
            
            OrderCreatedEvent event = new OrderCreatedEvent(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getUser().getId(),
                    order.getTotalAmount(),
                    itemEvents,
                    order.getShippingAddress(),
                    order.getCreatedAt()
            );
            
            kafkaProducerService.sendOrderCreatedEvent(event);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send OrderCreatedEvent: " + e.getMessage());
        }
    }
    
    /**
     * Send order status changed event to Kafka
     */
    private void sendOrderStatusChangedEvent(Order order, OrderStatus oldStatus) {
        try {
            OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getUser().getId(),
                    oldStatus != null ? oldStatus.name() : null,
                    order.getStatus().name(),
                    order.getUpdatedAt() != null ? order.getUpdatedAt() : LocalDateTime.now()
            );
            
            kafkaProducerService.sendOrderStatusChangedEvent(event);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send OrderStatusChangedEvent: " + e.getMessage());
        }
    }
    
    /**
     * Send inventory updated events to Kafka for all items in the order
     * @param order The order containing items
     * @param reason The reason for inventory update (ORDER_CREATED, ORDER_CANCELLED, ORDER_UPDATED)
     * @param oldStockMap Map of productId -> old stock value (before modification)
     */
    private void sendInventoryUpdatedEvents(Order order, String reason, java.util.Map<Long, Integer> oldStockMap) {
        try {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                Integer oldStock = oldStockMap.getOrDefault(product.getId(), product.getStock());
                Integer newStock = product.getStock();
                
                // Calculate quantity change based on reason
                Integer quantityChange;
                if ("ORDER_CANCELLED".equals(reason)) {
                    // For cancellation, stock was restored, so change is positive
                    quantityChange = item.getQuantity();
                } else {
                    // For creation/update, stock was deducted, so change is negative
                    quantityChange = -item.getQuantity();
                }
                
                InventoryUpdatedEvent event = new InventoryUpdatedEvent(
                        product.getId(),
                        product.getName(),
                        oldStock,
                        newStock,
                        quantityChange,
                        reason,
                        order.getId(),
                        LocalDateTime.now()
                );
                
                kafkaProducerService.sendInventoryUpdatedEvent(event);
            }
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send InventoryUpdatedEvent: " + e.getMessage());
        }
    }
    
    /**
     * Send inventory updated events for items that were restored (for order updates)
     * This handles the case where old items are removed from an order
     * @param order The order (items may have been cleared)
     * @param oldStockMap Map of productId -> stock value before restore (the low value)
     * @param restoredQuantities Map of productId -> quantity that was restored
     */
    private void sendInventoryUpdatedEventsForRestoredItems(Order order, java.util.Map<Long, Integer> oldStockMap, java.util.Map<Long, Integer> restoredQuantities) {
        try {
            for (java.util.Map.Entry<Long, Integer> entry : oldStockMap.entrySet()) {
                Long productId = entry.getKey();
                Integer oldStock = entry.getValue(); // Stock before restore (low value)
                Integer restoredQuantity = restoredQuantities.getOrDefault(productId, 0);
                
                if (restoredQuantity == 0) {
                    continue; // Skip if no quantity was restored
                }
                
                Product product = productRepository.findById(productId)
                        .orElse(null);
                if (product == null) {
                    continue;
                }
                
                Integer newStock = product.getStock(); // Stock after restore (high value)
                
                InventoryUpdatedEvent event = new InventoryUpdatedEvent(
                        product.getId(),
                        product.getName(),
                        oldStock,
                        newStock,
                        restoredQuantity, // Positive quantity change (stock was restored)
                        "ORDER_UPDATED_ITEM_REMOVED",
                        order.getId(),
                        LocalDateTime.now()
                );
                
                kafkaProducerService.sendInventoryUpdatedEvent(event);
            }
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send InventoryUpdatedEvent for restored items: " + e.getMessage());
        }
    }
}


