package com.pe.inventoryapp.backend.movement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.movement.model.entity.Movement_StockLot;

public interface Movement_StockLotRepository extends JpaRepository<Movement_StockLot, Long> {
  // OBTENER LOTES DE STOCKS TOMADOS POR ID DE MOVIMIENTO
  @Query("SELECT m FROM Movement_StockLot m WHERE m.movement.id = :movementId")
  List<Movement_StockLot> findAllByMovementId(@Param("movementId") Long movementId);
}
