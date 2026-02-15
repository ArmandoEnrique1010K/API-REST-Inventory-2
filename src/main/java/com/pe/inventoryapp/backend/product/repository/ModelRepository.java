package com.pe.inventoryapp.backend.product.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.product.model.entity.Model;

public interface ModelRepository extends JpaRepository<Model, Long> {

  // TODO: ¿DEBE INCLUIR SOLAMENTE TODOS LOS PRODUCTOS CUYO ESTADO DE CATEGORIA SEA TRUE?
  // Query personalizado para buscar productos mediante parametros
  // Nota: Lista los productos cuya categoria este activa
  @Query("""
          SELECT m
          FROM Model m
          JOIN m.product p
          WHERE (LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:minStock IS NULL OR m.quantityAvailable >= :minStock)
          AND (:maxStock IS NULL OR m.quantityAvailable <= :maxStock)
          AND (:minEntryDate IS NULL OR m.entryDate >= :minEntryDate)
          AND (:maxEntryDate IS NULL OR m.entryDate <= :maxEntryDate)
          AND (m.status IS NULL OR m.status = :status)
          AND (:categoryId IS NULL OR p.category.id = :categoryId)
          AND (:typeId IS NULL OR p.type.id = :typeId)
          AND p.category.status = true ORDER BY m.updatedAt DESC
      """)
  Page<Model> findAllByParams(
      Pageable pageable,
      @Param("keyword") String keyword,
      @Param("minStock") Integer minStock,
      @Param("maxStock") Integer maxStock,
      @Param("minEntryDate") LocalDate minEntryDate,
      @Param("maxEntryDate") LocalDate maxEntryDate,
      @Param("status") Boolean status,
      @Param("categoryId") Long categoryId,
      @Param("typeId") Long typeId
    );

  List<Model> findAllByProductId(Long productId);

  // Método para verificar que el nombre de modelo sea unico dentro de la lista de modelos de un producto por id
  boolean existsByNameAndProductId(String name, Long productId);

  // Método para verificar que el nombre de modelo sea unico dentro de la lista de modelos de un producto por id, excluyendo un id de modelo específico (para actualizaciones)
  boolean existsByNameAndProductIdAndIdNot(String name, Long productId, Long id);
}
