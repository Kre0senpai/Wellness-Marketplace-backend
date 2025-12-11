package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.Product;
import com.wellness.wellness_backend.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    // only PRACTITIONER or ADMIN can create products
    @PreAuthorize("hasRole('PRACTITIONER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product p, Authentication auth) {
        // ensure owner is taken from authenticated user — do NOT trust client-provided owner
        String email = (auth != null) ? auth.getName() : null;
        if (email != null) {
            p.setOwnerEmail(email.toLowerCase().trim());
        }
        Product saved = service.create(p);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Product> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Product p = service.getById(id);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(p);
    }
    
    // owner or admin can update
    @PreAuthorize("hasRole('ADMIN') or @productService.isOwner(#id, authentication.name)")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product update, Authentication auth) {
        Product existing = service.getById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        update.setId(id);
        update.setOwnerEmail(existing.getOwnerEmail()); // preserve owner
        Product saved = service.update(id, update);
        return ResponseEntity.ok(saved);
    }

    // owner or admin can delete
    @PreAuthorize("hasRole('ADMIN') or @productService.isOwner(#id, authentication.name)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean ok = service.delete(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    
    @GetMapping("/debug-auth")
    public Map<String,Object> debugAuth() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return Map.of(
            "principal", a==null?null:a.getPrincipal(),
            "name", a==null?null:a.getName(),
            "authenticated", a==null?false:a.isAuthenticated(),
            "authorities", a==null?null:a.getAuthorities()
        );
    }
}
