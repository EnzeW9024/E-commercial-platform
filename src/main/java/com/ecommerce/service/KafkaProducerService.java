package com.ecommerce.service;

import com.ecommerce.event.InventoryUpdatedEvent;
import com.ecommerce.event.OrderCreatedEvent;
import com.ecommerce.event.OrderStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer Service
 * Handles sending events to Kafka topics
 */
@Service
public class KafkaProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    
    // Kafka Topics
    public static final String TOPIC_ORDERS = "orders";
    public static final String TOPIC_ORDER_STATUS = "order-status";
    public static final String TOPIC_INVENTORY = "inventory";
    public static final String TOPIC_NOTIFICATIONS = "notifications";
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    /**
     * Send order created event
     */
    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        try {
            logger.info("Sending OrderCreatedEvent: orderId={}, orderNumber={}", 
                       event.getOrderId(), event.getOrderNumber());
            
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(TOPIC_ORDERS, String.valueOf(event.getOrderId()), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("OrderCreatedEvent sent successfully: orderId={}, offset={}", 
                               event.getOrderId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send OrderCreatedEvent: orderId={}", 
                                event.getOrderId(), ex);
                }
            });
        } catch (Exception e) {
            logger.error("Error sending OrderCreatedEvent: orderId={}", 
                        event.getOrderId(), e);
        }
    }
    
    /**
     * Send order status changed event
     */
    public void sendOrderStatusChangedEvent(OrderStatusChangedEvent event) {
        try {
            logger.info("Sending OrderStatusChangedEvent: orderId={}, oldStatus={}, newStatus={}", 
                       event.getOrderId(), event.getOldStatus(), event.getNewStatus());
            
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(TOPIC_ORDER_STATUS, String.valueOf(event.getOrderId()), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("OrderStatusChangedEvent sent successfully: orderId={}, offset={}", 
                               event.getOrderId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send OrderStatusChangedEvent: orderId={}", 
                                event.getOrderId(), ex);
                }
            });
        } catch (Exception e) {
            logger.error("Error sending OrderStatusChangedEvent: orderId={}", 
                        event.getOrderId(), e);
        }
    }
    
    /**
     * Send inventory updated event
     */
    public void sendInventoryUpdatedEvent(InventoryUpdatedEvent event) {
        try {
            logger.info("Sending InventoryUpdatedEvent: productId={}, oldStock={}, newStock={}", 
                       event.getProductId(), event.getOldStock(), event.getNewStock());
            
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(TOPIC_INVENTORY, String.valueOf(event.getProductId()), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("InventoryUpdatedEvent sent successfully: productId={}, offset={}", 
                               event.getProductId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send InventoryUpdatedEvent: productId={}", 
                                event.getProductId(), ex);
                }
            });
        } catch (Exception e) {
            logger.error("Error sending InventoryUpdatedEvent: productId={}", 
                        event.getProductId(), e);
        }
    }
    
    /**
     * Send notification event (generic method)
     */
    public void sendNotificationEvent(String userId, String message, String type) {
        try {
            Map<String, Object> notification = Map.of(
                "userId", userId,
                "message", message,
                "type", type,
                "timestamp", System.currentTimeMillis()
            );
            
            logger.info("Sending notification: userId={}, type={}", userId, type);
            
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(TOPIC_NOTIFICATIONS, userId, notification);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Notification sent successfully: userId={}, offset={}", 
                               userId, result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send notification: userId={}", userId, ex);
                }
            });
        } catch (Exception e) {
            logger.error("Error sending notification: userId={}", userId, e);
        }
    }
}

