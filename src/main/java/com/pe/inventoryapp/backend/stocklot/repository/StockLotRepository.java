package com.pe.inventoryapp.backend.stocklot.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;

public interface StockLotRepository extends JpaRepository<StockLot, Long> {

  Optional<StockLot> findByProductId(Long productId);

  // Obtiene la sumatoria de todos los campos stock de un producto por su id
  @Query("""
      SELECT COALESCE(SUM(sl.quantityAvailable), 0)
      FROM StockLot sl
      WHERE sl.product.id = :productId
  """)
  Integer sumAvailableByProductId(@Param("productId") Long productId);

  // Se reutiliza el último StockLot creado en las últimas 24h
  // para consolidar devoluciones
  Optional<StockLot> findTopByProductIdAndCreatedAtAfterOrderByCreatedAtDesc(
      Long productId,
      LocalDateTime limitCreatedAt);


    // Query para busqueda de lotes de stock mediante los siguientes parametros:
    // Integer minQuantityAvailable,
    // Integer maxQuantityAvailable,
    // LocalDateTime minCreatedAt,
    // LocalDateTime maxCreatedAt,
    // String productName,
    @Query("""
            SELECT sl
            FROM StockLot sl
            WHERE (:minQuantityAvailable IS NULL OR sl.quantityAvailable >= :minQuantityAvailable)
            AND (:maxQuantityAvailable IS NULL OR sl.quantityAvailable <= :maxQuantityAvailable)
            AND (:minCreatedAt IS NULL OR sl.createdAt >= :minCreatedAt)
            AND (:maxCreatedAt IS NULL OR sl.createdAt <= :maxCreatedAt)
            AND (:productName IS NULL OR LOWER(sl.product.name) LIKE LOWER(CONCAT('%', :productName, '%')))
            """)
    Page<StockLot> findAllByParams(
        @Param("minQuantityAvailable") Integer minQuantityAvailable,
        @Param("maxQuantityAvailable") Integer maxQuantityAvailable,
        @Param("minCreatedAt") LocalDateTime minCreatedAt,
        @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
        @Param("productName") String productName,
        Pageable pageable
    );
}
