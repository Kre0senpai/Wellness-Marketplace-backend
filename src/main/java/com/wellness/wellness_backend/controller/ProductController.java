package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.Product;
import com.wellness.wellness_backend.service.PractitionerService;
import com.wellness.wellness_backend.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final PractitionerService practitionerService;

    public ProductController(ProductService productService,
                             PractitionerService practitionerService) {
        this.productService = productService;
        this.practitionerService = practitionerService;
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product,
                                          Authentication auth) {

        String email = auth.getName(); // 👈 THIS IS EMAIL IN YOUR APP

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isVerifiedPractitioner =
                practitionerService.isVerifiedPractitionerByEmail(email);

        if (!isAdmin && !isVerifiedPractitioner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Product saved = productService.create(product, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @RequestBody Product update,
            Authentication auth
    ) {

        String email = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // only owner or admin allowed
        Product existing = productService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        if (!isAdmin && !existing.getOwnerEmail().equalsIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Product saved = productService.update(id, update);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication auth
    ) {

        String email = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Product existing = productService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // only owner or admin allowed
        if (!isAdmin && !existing.getOwnerEmail().equalsIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        productService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    public List<Product> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        Product p = productService.getById(id);
        return p == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(p);
    }
}
