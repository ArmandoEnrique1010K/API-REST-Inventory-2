package com.pe.inventoryapp.backend.stock.repository;

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


}
