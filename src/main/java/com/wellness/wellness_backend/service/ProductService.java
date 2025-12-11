package com.wellness.wellness_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import com.wellness.wellness_backend.repo.ProductRepository;
import com.wellness.wellness_backend.model.Product;

import java.util.List;
import java.util.Optional;

@Service("productService")
public class ProductService {

    private final ProductRepository repo;
    public ProductService(ProductRepository repo) { this.repo = repo; }

    /**
     * Create a product and set the owner from the authenticated user.
     * We always override any client-provided owner to prevent spoofing.
     */
    public Product create(Product p) {
        String email = getCurrentUsername();
        if (email == null) {
            throw new AccessDeniedException("Unauthenticated user cannot create product");
        }
        p.setOwnerEmail(email);
        return repo.save(p);
    }

    public List<Product> getAll() { return repo.findAll(); }

    public Product getById(Long id) { return repo.findById(id).orElse(null); }

    /**
     * Update product by id with ownership/admin check.
     * We fetch the existing entity, check permissions, apply allowed updates,
     * preserve ownerEmail, then save.
     */
    public Product update(Long id, Product incoming) {
        Product existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

        String current = getCurrentUsername();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            String owner = existing.getOwnerEmail();
            if (owner == null || current == null || !owner.equalsIgnoreCase(current)) {
                throw new AccessDeniedException("Not owner");
            }
        }

        // apply allowed updates (do not allow ownerEmail change)
        existing.setName(incoming.getName());
        existing.setDescription(incoming.getDescription());
        existing.setPrice(incoming.getPrice());
        existing.setStock(incoming.getStock());
        existing.setCategory(incoming.getCategory());

        return repo.save(existing);
    }

    public boolean delete(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }

    /**
     * SpEL-exposed owner check for controller-level @PreAuthorize:
     * true if the userEmail matches product.ownerEmail (case-insensitive).
     */
    public boolean isOwner(Long productId, String userEmail) {
        if (userEmail == null) return false;
        Optional<Product> o = repo.findById(productId);
        return o.isPresent() && o.get().getOwnerEmail() != null &&
               o.get().getOwnerEmail().equalsIgnoreCase(userEmail);
    }

    // helper: get the current authenticated username/email (or null)
    private String getCurrentUsername() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) return null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return (name == null) ? null : name.toLowerCase().trim();
    }
}
