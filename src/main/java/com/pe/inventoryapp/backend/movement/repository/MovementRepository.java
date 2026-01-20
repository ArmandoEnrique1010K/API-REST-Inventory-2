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
      AND (:username IS NULL OR 
        LOWER(m.user.firstname) LIKE LOWER(CONCAT('%', :username, '%')) OR 
        LOWER(m.user.lastname) LIKE LOWER(CONCAT('%', :username, '%')) OR
        LOWER(m.user.email) LIKE LOWER(CONCAT('%', :username, '%')) OR
        CAST(m.user.dni AS string) LIKE CONCAT('%', :username, '%'))
      AND (:productName IS NULL OR LOWER(m.product.name) LIKE LOWER(CONCAT('%', :productName, '%'))) 
      ORDER BY m.createdAt DESC
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
        SELECT m
        FROM Movement m
        JOIN m.deliveryLine dl
        WHERE (dl.id = :deliveryLineId)
        AND (:minQuantity IS NULL OR m.quantity >= :minQuantity)
        AND (:maxQuantity IS NULL OR m.quantity <= :maxQuantity)
        AND (:minCreatedAt IS NULL OR m.createdAt >= :minCreatedAt)
        AND (:maxCreatedAt IS NULL OR m.createdAt <= :maxCreatedAt)
        AND (:movementType IS NULL OR m.movementType = :movementType)
        AND (
          :username IS NULL OR
          LOWER(m.user.firstname) LIKE LOWER(CONCAT('%', :username, '%')) OR
          LOWER(m.user.lastname) LIKE LOWER(CONCAT('%', :username, '%')) OR
          LOWER(m.user.email) LIKE LOWER(CONCAT('%', :username, '%')) OR
          CAST(m.user.dni AS string) LIKE CONCAT('%', :username, '%')
        )
        AND (:productName IS NULL OR LOWER(m.product.name) LIKE LOWER(CONCAT('%', :productName, '%')))
        ORDER BY m.createdAt DESC
      """)

  Page<Movement> findAllByDeliveryLineIdAndParams(
    Long deliveryLineId,
      @Param("minQuantity") Integer minQuantity,
      @Param("maxQuantity") Integer maxQuantity,
      @Param("minCreatedAt") LocalDateTime minCreatedAt,
      @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
      @Param("movementType") MovementType movementType,
      @Param("username") String username,
      @Param("productName") String productName,
      Pageable pageable 
  );


  // La entidad Movement tiene 3 relaciones:
  // - 2 relaciones muchos a uno con StockLot (emitter y receiver)
  // - 1 relacion muchos a muchos con StocksLot (lista de lotes de stocks que fueron tomados para una linea de entrega)
  @Query("""
      SELECT DISTINCT m
      FROM Movement m
      LEFT JOIN m.stockLotDetails ms
      LEFT JOIN ms.stockLot sl ON sl.id = :stockLotId
      WHERE (
         sl.id = :stockLotId
         OR m.stockLotEmitter.id = :stockLotId
         OR m.stockLotReceiver.id = :stockLotId
      )
      AND (:minQuantity IS NULL OR m.quantity >= :minQuantity)
      AND (:maxQuantity IS NULL OR m.quantity <= :maxQuantity)
      AND (:minCreatedAt IS NULL OR m.createdAt >= :minCreatedAt)
      AND (:maxCreatedAt IS NULL OR m.createdAt <= :maxCreatedAt)
      AND (:movementType IS NULL OR m.movementType = :movementType)
      AND (:username IS NULL OR
        LOWER(m.user.firstname) LIKE LOWER(CONCAT('%', :username, '%')) OR
        LOWER(m.user.lastname) LIKE LOWER(CONCAT('%', :username, '%')) OR
        LOWER(m.user.email) LIKE LOWER(CONCAT('%', :username, '%')) OR
        CAST(m.user.dni AS string) LIKE CONCAT('%', :username, '%'))
      AND (:productName IS NULL OR LOWER(m.product.name) LIKE LOWER(CONCAT('%', :productName, '%')))
      ORDER BY m.createdAt DESC
      """)
  Page<Movement> findAllByStockLotIdAndParams(
      Long stockLotId,
      @Param("minQuantity") Integer minQuantity,
      @Param("maxQuantity") Integer maxQuantity,
      @Param("minCreatedAt") LocalDateTime minCreatedAt,
      @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
      @Param("movementType") MovementType movementType,
      @Param("username") String username,
      @Param("productName") String productName,
      Pageable pageable);

  @Query("""
      SELECT DISTINCT m
      FROM Movement m
      LEFT JOIN m.product p
      WHERE (p.id = :productId)
      AND (:minQuantity IS NULL OR m.quantity >= :minQuantity)
      AND (:maxQuantity IS NULL OR m.quantity <= :maxQuantity)
      AND (:minCreatedAt IS NULL OR m.createdAt >= :minCreatedAt)
      AND (:maxCreatedAt IS NULL OR m.createdAt <= :maxCreatedAt)
      AND (:movementType IS NULL OR m.movementType = :movementType)
      AND (:username IS NULL OR
        LOWER(m.user.firstname) LIKE LOWER(CONCAT('%', :username, '%')) OR
        LOWER(m.user.lastname) LIKE LOWER(CONCAT('%', :username, '%')) OR
        LOWER(m.user.email) LIKE LOWER(CONCAT('%', :username, '%')) OR
        CAST(m.user.dni AS string) LIKE CONCAT('%', :username, '%'))
      ORDER BY m.createdAt DESC
      """)
  Page<Movement> findAllByProductIdAndParams(
      Long productId,
      @Param("minQuantity") Integer minQuantity,
      @Param("maxQuantity") Integer maxQuantity,
      @Param("minCreatedAt") LocalDateTime minCreatedAt,
      @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
      @Param("movementType") MovementType movementType,
      @Param("username") String username,
      Pageable pageable);

  @Query("""
      SELECT DISTINCT m
      FROM Movement m
      LEFT JOIN m.user u
      WHERE (u.id = :userId)
      AND (:minQuantity IS NULL OR m.quantity >= :minQuantity)
      AND (:maxQuantity IS NULL OR m.quantity <= :maxQuantity)
      AND (:minCreatedAt IS NULL OR m.createdAt >= :minCreatedAt)
      AND (:maxCreatedAt IS NULL OR m.createdAt <= :maxCreatedAt)
      AND (:movementType IS NULL OR m.movementType = :movementType)
      AND (:productName IS NULL OR LOWER(m.product.name) LIKE LOWER(CONCAT('%', :productName, '%')))
      ORDER BY m.createdAt DESC
      """)
  Page<Movement> findAllByUserIdAndParams(
      Long userId,
      @Param("minQuantity") Integer minQuantity,
      @Param("maxQuantity") Integer maxQuantity,
      @Param("minCreatedAt") LocalDateTime minCreatedAt,
      @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
      @Param("movementType") MovementType movementType,
      @Param("productName") String productName,
      Pageable pageable);

}
