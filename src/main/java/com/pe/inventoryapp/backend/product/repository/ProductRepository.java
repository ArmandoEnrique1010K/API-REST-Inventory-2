package com.pe.inventoryapp.backend.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.product.model.entity.Product;

// Si un query tiene al menos 4 filtros simples, sin JOINs explicitos complejos o sin logica avanzada (GROUP BY, HAVING)
// No necesita un Specification, pero si llegara a pasar los 10000 registros, si seria necesario implementar un Specification
public interface ProductRepository extends JpaRepository<Product, Long> {

  //* RECORDAR QUE UN PAGE HACE 2 QUERIES EN CONSOLA: 1 SELECT Y 1 COUNT
  @Query("""
      SELECT p
      FROM Product p
      JOIN FETCH p.category c
      JOIN FETCH p.type t
      WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:status IS NULL OR p.status = :status)
        AND (:categoryId IS NULL OR c.id = :categoryId)
        AND (:typeId IS NULL OR t.id = :typeId)
      """)
  Page<Product> findAllByParams(
    Pageable pageable, 
    @Param("name") String name, 
    @Param("status") Boolean status, 
    @Param("categoryId") Long categoryId,
    @Param("typeId") Long typeId);

  boolean existsByName(String name);
  boolean existsByNameAndIdNot(String name, Long id);

  @Query("""
      SELECT p FROM Product p
      JOIN FETCH p.category c
      JOIN FETCH p.type t
      WHERE p.id = :id
      """)
  Optional<Product> findByIdFull(Long id);

}
