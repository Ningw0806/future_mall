package com.future.itemservice.repository;

import com.future.itemservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByNameLikeIgnoreCase(String keyword);
}
