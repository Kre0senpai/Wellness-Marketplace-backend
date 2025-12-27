package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.dto.CartDTO;
import com.wellness.wellness_backend.dto.AddCartItemRequest;
import com.wellness.wellness_backend.dto.UpdateQuantityRequest;
import com.wellness.wellness_backend.mapper.CartMapper;
import com.wellness.wellness_backend.model.Cart;
import com.wellness.wellness_backend.service.CartService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ============================
    // GET CURRENT USER CART
    // ============================
    @GetMapping
    public CartDTO getCurrentUserCart(Authentication auth) {
        Cart cart = cartService.getOrCreateCart(auth.getName());
        return CartMapper.toDTO(cart);
    }

    // ============================
    // ADD ITEM TO CART
    // ============================
    @PostMapping("/items")
    public CartDTO addItem(
            @Valid @RequestBody AddCartItemRequest req,
            Authentication auth
    ) {
        Cart cart = cartService.addItem(
                auth.getName(),
                req.getProductId(),
                req.getQuantity()
        );
        return CartMapper.toDTO(cart);
    }

    // ============================
    // UPDATE ITEM QUANTITY
    // ============================
    @PutMapping("/items/{id}")
    public CartDTO updateItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuantityRequest req,
            Authentication auth
    ) {
        Cart cart = cartService.updateItem(
                id,
                req.getQuantity(),
                auth.getName()
        );
        return CartMapper.toDTO(cart);
    }

    // ============================
    // REMOVE ITEM FROM CART
    // ============================
    @DeleteMapping("/items/{id}")
    public CartDTO removeItem(
            @PathVariable Long id,
            Authentication auth
    ) {
        Cart cart = cartService.removeItem(id, auth.getName());
        return CartMapper.toDTO(cart);
    }

    // ============================
    // CLEAR CART
    // ============================
    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication auth) {
        cartService.clearCart(auth.getName());
        return ResponseEntity.noContent().build();
    }
}
