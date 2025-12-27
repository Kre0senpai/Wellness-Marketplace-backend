package com.wellness.wellness_backend.service;

import com.wellness.wellness_backend.model.Product;
import com.wellness.wellness_backend.repo.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product create(Product p, String ownerEmail) {
        p.setOwnerEmail(ownerEmail);
        return repo.save(p);
    }
    
    public Product update(Long id, Product incoming) {
        Product existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (incoming.getName() != null)
            existing.setName(incoming.getName());

        if (incoming.getDescription() != null)
            existing.setDescription(incoming.getDescription());

        if (incoming.getPrice() != null)
            existing.setPrice(incoming.getPrice());

        if (incoming.getStock() != null)
            existing.setStock(incoming.getStock());

        if (incoming.getCategory() != null)
            existing.setCategory(incoming.getCategory());

        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }


    public List<Product> getAll() {
        return repo.findAll();
    }

    public Product getById(Long id) {
        return repo.findById(id).orElse(null);
    }
}
