package com.pe.inventoryapp.backend.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.product.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
  @Query("""
      SELECT p
      FROM Product p
      WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:minStock IS NULL OR p.stock >= :minStock)
        AND (:maxStock IS NULL OR p.stock <= :maxStock)
        AND (:categoryId IS NULL OR p.category.id = :categoryId)
        AND (:status IS NULL OR p.status = :status)
        """)
  Page<Product> findAllByParams(
      @Param("name") String name,
      @Param("minStock") Integer minStock,
      @Param("maxStock") Integer maxStock,
      @Param("categoryId") Long categoryId,
      @Param("status") Boolean status,
      Pageable pageable);

  @Query("""
          SELECT p
          FROM Product p
          WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:minStock IS NULL OR p.stock >= :minStock)
            AND (:maxStock IS NULL OR p.stock <= :maxStock)
            AND (:categoryId IS NULL OR p.category.id = :categoryId)
            AND p.status = true
      """)
  Page<Product> findAllByNameAndStatusTrue(
      @Param("name") String name,
      @Param("minStock") Integer minStock,
      @Param("maxStock") Integer maxStock,
      @Param("categoryId") Long categoryId,
      Pageable pageable);

  Optional<Product> findByName(String name);
}
