package com.wellness.wellness_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wellness.wellness_backend.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}