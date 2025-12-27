package com.wellness.wellness_backend.dto;

public class ProductDTO {
    private Long id;
    private String name;
    private Double price;

    public ProductDTO(Long id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
}
