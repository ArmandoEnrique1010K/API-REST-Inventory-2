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
      SELECT m
      FROM Movement m
      WHERE (:minQuantity IS NULL OR m.quantity >= :minQuantity)
      AND (:maxQuantity IS NULL OR m.quantity <= :maxQuantity)
      AND (:minCreatedAt IS NULL OR m.createdAt >= :minCreatedAt)
      AND (:maxCreatedAt IS NULL OR m.createdAt <= :maxCreatedAt)
      AND (:movementType IS NULL OR m.movementType = :movementType)
      AND (:username IS NULL OR LOWER(m.username_snapshot) LIKE LOWER(CONCAT('%', :username, '%')))
      AND (:productName IS NULL OR LOWER(m.product.name) LIKE LOWER(CONCAT('%', :productName, '%')))
      """)
  Page<Movement> findAllByParams(
    @Param("minQuantity") Integer minQuantity,
    @Param("maxQuantity") Integer maxQuantity,
    @Param("minCreatedAt") LocalDateTime minCreatedAt,
    @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
    @Param("movementType") MovementType movementType,
    @Param("username") String username,
    @Param("productName") String productName,
    Pageable pageable
  );

  @Query("""
        SELECT COALESCE(SUM(m.quantity), 0)
        FROM Movement m
        WHERE m.product.id = :productId
          AND m.movementType = :movementType
      """)
  Integer sumQuantityByProductAndType(
      @Param("productId") Long productId,
      @Param("movementType") MovementType movementType);
}
