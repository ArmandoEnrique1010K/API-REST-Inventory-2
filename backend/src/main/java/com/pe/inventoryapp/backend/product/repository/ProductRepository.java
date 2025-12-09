package com.pe.inventoryapp.backend.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.product.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Page<Product> findAllByStatusTrue(Pageable pageable);

  @Query("""
          SELECT p
          FROM Product p
          WHERE p.status = true
            AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
      """)
  Page<Product> findAllByNameAndStatusTrue(@Param("name") String name, Pageable pageable);

  @Query("""
          SELECT p
          FROM Product p
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
      """)
  Page<Product> findAllByName(@Param("name") String name, Pageable pageable);

  Optional<Product> findByName(String name);
}
