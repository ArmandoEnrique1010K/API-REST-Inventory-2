package com.pe.inventoryapp.backend.movement.service;

import java.util.List;

import com.pe.inventoryapp.backend.movement.model.response.Movement_StockLotResponse;

public interface Movement_StockLotService {
  List<Movement_StockLotResponse> findAllByMovementId(Long movementId);
}
