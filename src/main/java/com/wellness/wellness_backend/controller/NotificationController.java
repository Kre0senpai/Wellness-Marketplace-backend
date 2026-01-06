package com.wellness.wellness_backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Controller
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Send notification to specific user
     * 
     * @param userId User ID to send notification to
     * @param message Notification message
     */
    public void sendNotificationToUser(Long userId, Map<String, Object> message) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(), 
            "/notifications", 
            message
        );
    }

    /**
     * Send booking update to specific user
     */
    public void sendBookingUpdate(Long userId, Map<String, Object> booking) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(), 
            "/bookings", 
            booking
        );
    }

    /**
     * Send order update to specific user
     */
    public void sendOrderUpdate(Long userId, Map<String, Object> order) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(), 
            "/orders", 
            order
        );
    }
    
 // In NotificationController.java
    @GetMapping("/test-notification/{userId}")
    public ResponseEntity<?> testNotification(@PathVariable Long userId) {
        Map<String, Object> notification = Map.of(
            "type", "notification",
            "message", "Test notification from backend!",
            "timestamp", System.currentTimeMillis()
        );
        
        messagingTemplate.convertAndSendToUser(
            userId.toString(), 
            "/notifications", 
            notification
        );
        
        return ResponseEntity.ok("Notification sent to user " + userId);
    }

    /**
     * Broadcast to all users (for system announcements)
     */
    @MessageMapping("/broadcast")
    @SendTo("/topic/announcements")
    public Map<String, Object> broadcast(Map<String, Object> message) {
        return message;
    }
}