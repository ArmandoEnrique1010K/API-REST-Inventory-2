package com.pe.inventoryapp.backend.movement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.movement.model.entity.Movement_StockLot;

public interface Movement_StockLotRepository extends JpaRepository<Movement_StockLot, Long> {
  
}
