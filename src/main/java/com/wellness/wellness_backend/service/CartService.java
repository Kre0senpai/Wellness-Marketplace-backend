package com.wellness.wellness_backend.service;

import com.wellness.wellness_backend.model.Cart;
import com.wellness.wellness_backend.model.CartItem;
import com.wellness.wellness_backend.model.Product;
import com.wellness.wellness_backend.repo.CartItemRepository;
import com.wellness.wellness_backend.repo.CartRepository;
import com.wellness.wellness_backend.repo.ProductRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    // =========================
    // GET OR CREATE CART
    // =========================
    public Cart getOrCreateCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserEmail(userEmail);
                    return cartRepository.save(cart);
                });
    }

    // =========================
    // ADD ITEM TO CART
    // =========================
    public Cart addItem(String userEmail, Long productId, int quantity) {

        if (quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be greater than 0"
            );
        }

        // 🔥 FIX: Proper product lookup with 404
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with id " + productId
                ));

        Cart cart = getOrCreateCart(userEmail);

        // Check if product already in cart
        Optional<CartItem> existingItemOpt =
                cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPriceAtAddTime(product.getPrice());
            cartItemRepository.save(item);
        }

        return getOrCreateCart(userEmail);
    }

    // =========================
    // UPDATE ITEM QUANTITY
    // =========================
    public Cart updateItem(Long cartItemId, int quantity, String userEmail) {

        if (quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be greater than 0"
            );
        }

        Cart cart = getOrCreateCart(userEmail);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cart item not found"
                ));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return cart;
    }

    // =========================
    // REMOVE ITEM
    // =========================
    public Cart removeItem(Long cartItemId, String userEmail) {

        Cart cart = getOrCreateCart(userEmail);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cart item not found"
                ));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        cartItemRepository.delete(item);
        return cart;
    }

    // =========================
    // CLEAR CART
    // =========================
    public void clearCart(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);
        cartItemRepository.deleteAll(cart.getItems());
    }
}
