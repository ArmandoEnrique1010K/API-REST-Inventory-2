package com.pe.inventoryapp.backend.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.product.model.entity.Product;

// Si un query tiene al menos 4 filtros simples, sin JOINs explicitos complejos o sin logica avanzada (GROUP BY, HAVING)
// No necesita un Specification, pero si llegara a pasar los 10000 registros, si seria necesario implementar un Specification
public interface ProductRepository extends JpaRepository<Product, Long> {
  @Query("""
      SELECT p
      FROM Product p
      WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:status IS NULL OR p.status = :status)
        AND (:categoryId IS NULL OR p.category.id = :categoryId)
        AND (:typeId IS NULL OR p.type.id = :typeId)
        ORDER BY p.id DESC
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
