package com.wellness.wellness_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CartDTO {

    private Long id;
    private String userEmail;
    private LocalDateTime createdAt;
    private List<CartItemDTO> items;

    public CartDTO(
            Long id,
            String userEmail,
            LocalDateTime createdAt,
            List<CartItemDTO> items
    ) {
        this.id = id;
        this.userEmail = userEmail;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<CartItemDTO> getItems() { return items; }
}
