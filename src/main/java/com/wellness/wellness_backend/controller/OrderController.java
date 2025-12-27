package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.Order;
import com.wellness.wellness_backend.model.OrderStatus;
import com.wellness.wellness_backend.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =========================
    // CHECKOUT
    // =========================
    @PostMapping("/checkout")
    public Order checkout(Authentication auth) {
        return orderService.checkout(auth.getName());
    }

    // =========================
    // GET MY ORDERS
    // =========================
    @GetMapping
    public List<Order> myOrders(Authentication auth) {
        return orderService.getOrdersByUser(auth.getName());
    }

    // =========================
    // UPDATE ORDER STATUS (ADMIN)
    // =========================
    @PutMapping("/{id}/status")
    public Order updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {
        return orderService.updateStatus(id, status);
    }
}
