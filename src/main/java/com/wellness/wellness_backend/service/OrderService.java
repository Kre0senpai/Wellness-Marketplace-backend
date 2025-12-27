package com.wellness.wellness_backend.service;

import com.wellness.wellness_backend.model.*;
import com.wellness.wellness_backend.repo.CartRepository;
import com.wellness.wellness_backend.repo.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    public OrderService(
            CartRepository cartRepository,
            OrderRepository orderRepository
    ) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    // ============================
    // CHECKOUT
    // ============================
    @Transactional
    public Order checkout(String userEmail) {

        Cart cart = cartRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUserEmail(userEmail);

        double total = 0;

        for (CartItem ci : cart.getItems()) {

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setPriceAtPurchase(ci.getProduct().getPrice());

            total += ci.getProduct().getPrice() * ci.getQuantity();
            order.getItems().add(oi);
        }

        order.setTotalAmount(total);

        // clear cart
        cart.getItems().clear();

        return orderRepository.save(order);
    }

    // ============================
    // GET ORDERS BY USER
    // ============================
    public List<Order> getOrdersByUser(String userEmail) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
    }

    // ============================
    // UPDATE ORDER STATUS (ADMIN)
    // ============================
    @Transactional
    public Order updateStatus(Long orderId, OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        return orderRepository.save(order);
    }
}
