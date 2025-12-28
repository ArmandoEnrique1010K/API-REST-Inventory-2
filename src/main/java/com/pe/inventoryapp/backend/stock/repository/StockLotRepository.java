package com.pe.inventoryapp.backend.stock.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.stock.model.entity.StockLot;

public interface StockLotRepository extends JpaRepository<StockLot, Long> {

  Optional<StockLot> findByProductId(Long productId);

  // Obtiene la sumatoria de todos los campos stock de un producto por su id
  @Query("""
      SELECT COALESCE(SUM(sl.quantityAvailable), 0)
      FROM StockLot sl
      WHERE sl.product.id = :productId
  """)
  Integer sumAvailableByProductId(@Param("productId") Long productId);


 // Buscar un stock por el id del producto y una fecha de creación
 // Condición: la fecha de creación (createdAt) no debe exceder de 1 dia desde la fecha actual
  // @Query("""
  //         SELECT sl
  //   FROM StockLot sl
  //   WHERE sl.product.id = :idProduct
  //     AND sl.createdAt >= :limitCreatedAt
  //   ORDER BY sl.createdAt DESC
  //     """)
  // Optional<StockLot> findRecentByProductId(
  //     @Param("idProduct") Long idProduct,
  //     @Param("limitCreatedAt") LocalDateTime limitCreatedAt);


  // Se reutiliza el último StockLot creado en las últimas 24h
  // para consolidar devoluciones
  Optional<StockLot> findTopByProductIdAndCreatedAtAfterOrderByCreatedAtDesc(
      Long productId,
      LocalDateTime limitCreatedAt);
}
