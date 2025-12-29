package com.pe.inventoryapp.backend.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.product.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

  // Query personalizado para buscar productos mediante parametros
  // Nota: Lista los productos cuya categoria este activa
  @Query("""
      SELECT p 
      FROM Product p
      WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:minStock IS NULL OR p.stock >= :minStock)
        AND (:maxStock IS NULL OR p.stock <= :maxStock)
        AND (:status IS NULL OR p.status = :status)
        AND (:categoryId IS NULL OR p.category.id = :categoryId)
        AND p.category.status = true
        """)
  Page<Product> findAllByParams(
      @Param("name") String name,
      @Param("minStock") Integer minStock,
      @Param("maxStock") Integer maxStock,
      @Param("status") Boolean status,
      @Param("categoryId") Long categoryId,
      Pageable pageable);

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);
}
