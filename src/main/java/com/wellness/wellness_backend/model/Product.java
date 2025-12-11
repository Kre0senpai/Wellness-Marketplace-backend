package com.wellness.wellness_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    private Double price;
    private Integer stock;
    private String category;

    @Column(nullable = false)
    private String ownerEmail;

    public Product() {}

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getOwnerEmail() { return ownerEmail; }

    /**
     * Normalize owner email on set so comparisons are reliable.
     * Use lower-case trimmed email.
     */
    public void setOwnerEmail(String ownerEmail) {
        if (ownerEmail == null) {
            this.ownerEmail = null;
        } else {
            this.ownerEmail = ownerEmail.toLowerCase().trim();
        }
    }
}
