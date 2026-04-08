package com.pe.inventoryapp.backend.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.pe.inventoryapp.backend.product.model.entity.Model;

// Para implementar un Specification, añade "JpaSpecificationExecutor" seguido del nombre de la entidad
public interface ModelRepository extends JpaRepository<Model, Long>, JpaSpecificationExecutor<Model> {

  //* INVESTIGAR SOBRE SPECIFICATION EN SPRING BOOT

  // ESTO ES UNA MALA PRACTICA EN SISTEMAS GRANDES
  // Query personalizado para buscar productos mediante parametros
  // @Query("""
  //         SELECT m
  //         FROM Model m
  //         JOIN m.product p
  //         WHERE (
  //           :keyword IS NULL OR
  //           LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
  //           LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
  //         )
  //         AND (:minStock IS NULL OR m.totalQuantityAvailable >= :minStock)
  //         AND (:maxStock IS NULL OR m.totalQuantityAvailable <= :maxStock)
  //         AND (:minEntryDate IS NULL OR m.entryDate >= :minEntryDate)
  //         AND (:maxEntryDate IS NULL OR m.entryDate <= :maxEntryDate)
  //         AND (:status IS NULL OR m.status = :status)
  //         AND (:categoryId IS NULL OR p.category.id = :categoryId)
  //         AND (:typeId IS NULL OR p.type.id = :typeId)
  //         ORDER BY m.id DESC
  //     """)
  // Page<Model> findAllByParams(
  //     Pageable pageable,
  //     @Param("keyword") String keyword,
  //     @Param("minStock") Integer minStock,
  //     @Param("maxStock") Integer maxStock,
  //     @Param("minEntryDate") LocalDate minEntryDate,
  //     @Param("maxEntryDate") LocalDate maxEntryDate,
  //     @Param("status") Boolean status,
  //     @Param("categoryId") Long categoryId,
  //     @Param("typeId") Long typeId);


  // En lugar de un Query complejo, se tiene que utilizar un Specification 






  // Query para listar todos los modelos y productos que esten activos, sin parametros
  // @Query("""
  //         SELECT m
  //         FROM Model m
  //         JOIN m.product p
  //         WHERE (
  //           :keyword IS NULL OR
  //           LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
  //           LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
  //         )
  //         AND m.status = TRUE AND p.status = TRUE
  //         ORDER BY m.id DESC
  //     """)
  // Page<Model> findAllActivesByName(
  //     Pageable pageable,
  //     @Param("keyword") String keyword);



  // Lista de los primeros 10 modelos que coincidan con el parametro
  // El producto y el modelo deben sus estados en true
  // @Query("""
  //     SELECT m 
  //     FROM Model m 
  //     JOIN m.product p
  //     WHERE m.status = true AND p.status = true
  //     AND (
  //       :keyword IS NULL OR 
  //       LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
  //       LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
  //     )
  //     ORDER BY m.id DESC LIMIT 10
  //     """)
  // List<Model> findAllFirstTenModelsByParams(@Param("keyword") String keyword);


  // Listar todos los modelos que pertenecen a un producto
  @Query("SELECT m FROM Model m WHERE m.product.id = :productId ORDER BY m.id DESC")
  List<Model> findAllByProductId(Long productId);

  // Método para verificar que el nombre de modelo sea unico dentro de la lista de
  // modelos de un producto por id
  boolean existsByNameAndProductId(String name, Long productId);

  // Método para verificar que el nombre de modelo sea unico dentro de la lista de
  // modelos de un producto por id, excluyendo un id de modelo específico (para
  // actualizaciones)
  boolean existsByNameAndProductIdAndIdNot(String name, Long productId, Long id);
}
