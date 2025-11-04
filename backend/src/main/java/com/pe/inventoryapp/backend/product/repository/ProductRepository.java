package com.pe.inventoryapp.backend.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.product.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

  Page<Product> findAllByStatusTrue(Pageable pageable);

  Optional<Product> findByName(String name);
}
