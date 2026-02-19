package com.pe.inventoryapp.backend.movement.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;

public interface MovementRepository extends JpaRepository<Movement, Long> {

  // Query personalizado para buscar movimientos mediante parametros

  @Query("""
      SELECT mv
      FROM Movement mv
      JOIN mv.model m
      JOIN m.product p
      JOIN mv.user u
      LEFT JOIN mv.deliveryLine dl
      LEFT JOIN mv.stockLot s
      WHERE (:minQuantity IS NULL OR mv.quantity >= :minQuantity)
      AND (:maxQuantity IS NULL OR mv.quantity <= :maxQuantity)
      AND (:minCreatedAt IS NULL OR mv.createdAt >= :minCreatedAt)
      AND (:maxCreatedAt IS NULL OR mv.createdAt <= :maxCreatedAt)
      AND (:movementType IS NULL OR mv.movementType = :movementType)
      AND (
        :username IS NULL OR 
        LOWER(u.firstname) LIKE LOWER(CONCAT('%', :username, '%')) OR 
        LOWER(u.lastname) LIKE LOWER(CONCAT('%', :username, '%')) OR
        LOWER(u.email) LIKE LOWER(CONCAT('%', :username, '%')) OR
        CAST(u.dni AS string) LIKE CONCAT('%', :username, '%'))
      AND (
        :keyword IS NULL OR
        LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
      AND (:deliveryLineId IS NULL OR dl.id = :deliveryLineId)
      AND (:modelId IS NULL OR m.id = :modelId)
      AND (:userId IS NULL OR u.id = :userId)
      ORDER BY mv.createdAt DESC
  """)
  Page<Movement> findAllByParams(
      Pageable pageable,
      @Param("minQuantity") Integer minQuantity,
      @Param("maxQuantity") Integer maxQuantity,
      @Param("minCreatedAt") LocalDateTime minCreatedAt,
      @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
      @Param("movementType") MovementType movementType,
      @Param("deliveryLineId") Long deliveryLineId,
      @Param("username") String username,
      @Param("keyword") String keyword,
      @Param("modelId") Long modelId,
      @Param("userId") Long userId);
}
