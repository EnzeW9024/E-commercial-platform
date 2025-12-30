package com.ecommerce.service;

import com.ecommerce.event.InventoryUpdatedEvent;
import com.ecommerce.event.OrderCreatedEvent;
import com.ecommerce.event.OrderStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Kafka Consumer Service
 * Handles consuming events from Kafka topics
 */
@Service
public class KafkaConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    
    /**
     * Consume order created events
     */
    @KafkaListener(topics = KafkaProducerService.TOPIC_ORDERS, groupId = "order-processor")
    public void consumeOrderCreatedEvent(
            @Payload OrderCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Received OrderCreatedEvent: orderId={}, orderNumber={}, topic={}, partition={}, offset={}",
                       event.getOrderId(), event.getOrderNumber(), topic, partition, offset);
            
            // Process order created event
            // Example: Send email notification, update analytics, etc.
            logger.info("Processing order: orderId={}, userId={}, totalAmount={}",
                       event.getOrderId(), event.getUserId(), event.getTotalAmount());
            
            // Simulate order processing
            processOrderCreatedEvent(event);
            
            // Acknowledge the message
            acknowledgment.acknowledge();
            
            logger.info("OrderCreatedEvent processed successfully: orderId={}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing OrderCreatedEvent: orderId={}", 
                        event.getOrderId(), e);
            // In production, you might want to send to a dead-letter queue
        }
    }
    
    /**
     * Consume order status changed events
     */
    @KafkaListener(topics = KafkaProducerService.TOPIC_ORDER_STATUS, groupId = "order-status-processor")
    public void consumeOrderStatusChangedEvent(
            @Payload OrderStatusChangedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Received OrderStatusChangedEvent: orderId={}, oldStatus={}, newStatus={}, topic={}, partition={}, offset={}",
                       event.getOrderId(), event.getOldStatus(), event.getNewStatus(), topic, partition, offset);
            
            // Process order status change event
            // Example: Send status update notification, update customer dashboard, etc.
            logger.info("Processing status change: orderId={}, status: {} -> {}",
                       event.getOrderId(), event.getOldStatus(), event.getNewStatus());
            
            // Simulate status change processing
            processOrderStatusChangedEvent(event);
            
            // Acknowledge the message
            acknowledgment.acknowledge();
            
            logger.info("OrderStatusChangedEvent processed successfully: orderId={}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing OrderStatusChangedEvent: orderId={}", 
                        event.getOrderId(), e);
        }
    }
    
    /**
     * Consume inventory updated events
     */
    @KafkaListener(topics = KafkaProducerService.TOPIC_INVENTORY, groupId = "inventory-processor")
    public void consumeInventoryUpdatedEvent(
            @Payload InventoryUpdatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Received InventoryUpdatedEvent: productId={}, oldStock={}, newStock={}, reason={}, topic={}, partition={}, offset={}",
                       event.getProductId(), event.getOldStock(), event.getNewStock(), 
                       event.getReason(), topic, partition, offset);
            
            // Process inventory update event
            // Example: Trigger reorder alert if stock is low, update analytics, etc.
            logger.info("Processing inventory update: productId={}, stock: {} -> {} (reason: {})",
                       event.getProductId(), event.getOldStock(), event.getNewStock(), event.getReason());
            
            // Simulate inventory processing
            processInventoryUpdatedEvent(event);
            
            // Acknowledge the message
            acknowledgment.acknowledge();
            
            logger.info("InventoryUpdatedEvent processed successfully: productId={}", event.getProductId());
        } catch (Exception e) {
            logger.error("Error processing InventoryUpdatedEvent: productId={}", 
                        event.getProductId(), e);
        }
    }
    
    /**
     * Consume notification events
     */
    @KafkaListener(topics = KafkaProducerService.TOPIC_NOTIFICATIONS, groupId = "notification-processor")
    public void consumeNotificationEvent(
            @Payload Map<String, Object> notification,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            String userId = (String) notification.get("userId");
            String message = (String) notification.get("message");
            String type = (String) notification.get("type");
            
            logger.info("Received notification: userId={}, type={}, message={}, topic={}, partition={}, offset={}",
                       userId, type, message, topic, partition, offset);
            
            // Process notification
            // Example: Send email, SMS, push notification, etc.
            logger.info("Processing notification: userId={}, type={}", userId, type);
            
            // Simulate notification sending
            processNotificationEvent(notification);
            
            // Acknowledge the message
            acknowledgment.acknowledge();
            
            logger.info("Notification processed successfully: userId={}", userId);
        } catch (Exception e) {
            logger.error("Error processing notification", e);
        }
    }
    
    /**
     * Process order created event
     */
    private void processOrderCreatedEvent(OrderCreatedEvent event) {
        // Implement order processing logic
        // Example: Send confirmation email, update analytics, etc.
        logger.debug("Order processing: orderId={}", event.getOrderId());
    }
    
    /**
     * Process order status changed event
     */
    private void processOrderStatusChangedEvent(OrderStatusChangedEvent event) {
        // Implement status change processing logic
        // Example: Send status update email, update customer dashboard, etc.
        logger.debug("Status change processing: orderId={}", event.getOrderId());
    }
    
    /**
     * Process inventory updated event
     */
    private void processInventoryUpdatedEvent(InventoryUpdatedEvent event) {
        // Implement inventory processing logic
        // Example: Check if reorder is needed, update analytics, etc.
        logger.debug("Inventory processing: productId={}", event.getProductId());
        
        // Example: Trigger reorder alert if stock is low
        if (event.getNewStock() < 10) {
            logger.warn("Low stock alert: productId={}, stock={}", 
                       event.getProductId(), event.getNewStock());
        }
    }
    
    /**
     * Process notification event
     */
    private void processNotificationEvent(Map<String, Object> notification) {
        // Implement notification sending logic
        // Example: Send email, SMS, push notification, etc.
        String userId = (String) notification.get("userId");
        logger.debug("Notification sending: userId={}", userId);
    }
}

