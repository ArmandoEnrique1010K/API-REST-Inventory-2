package com.pe.inventoryapp.backend.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.product.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

  // Query personalizado para buscar productos mediante parametros
  @Query("""
      SELECT p
      FROM Product p
      WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:status IS NULL OR p.status = :status)
        AND (:categoryId IS NULL OR p.category.id = :categoryId)
        AND (:typeId IS NULL OR p.type.id = :typeId)
        AND p.category.status = true ORDER BY p.updatedAt DESC
      """)
  Page<Product> findAllByParams(
    Pageable pageable, 
    @Param("name") String name, 
    @Param("status") Boolean status, 
    @Param("categoryId") Long categoryId,
    @Param("typeId") Long typeId);

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);
}
