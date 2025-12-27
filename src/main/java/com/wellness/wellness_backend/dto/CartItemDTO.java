package com.wellness.wellness_backend.dto;

public class CartItemDTO {

    private Long id;
    private ProductDTO product;
    private int quantity;
    private Double priceAtAddTime;

    public CartItemDTO(
            Long id,
            ProductDTO product,
            int quantity,
            Double priceAtAddTime
    ) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.priceAtAddTime = priceAtAddTime;
    }

    public Long getId() { return id; }
    public ProductDTO getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public Double getPriceAtAddTime() { return priceAtAddTime; }
}
