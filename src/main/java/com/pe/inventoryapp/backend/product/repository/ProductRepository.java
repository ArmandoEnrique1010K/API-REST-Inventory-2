package com.pe.inventoryapp.backend.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.product.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
  boolean existsByName(String name);
  boolean existsByNameAndIdNot(String name, Long id);
}
