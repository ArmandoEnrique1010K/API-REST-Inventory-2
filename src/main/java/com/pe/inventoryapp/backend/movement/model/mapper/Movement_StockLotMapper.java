package com.pe.inventoryapp.backend.movement.model.mapper;

import com.pe.inventoryapp.backend.movement.model.entity.Movement_StockLot;
import com.pe.inventoryapp.backend.movement.model.response.Movement_StockLotResponse;

public class Movement_StockLotMapper {
  private Movement_StockLot movement_StockLot;

  private Movement_StockLotMapper() {
  }

  public static Movement_StockLotMapper builder() {
    return new Movement_StockLotMapper();
  }

  public Movement_StockLotMapper setMovement_StockLot(Movement_StockLot movement_StockLot) {
    this.movement_StockLot = movement_StockLot;
    return this;
  }

  public Movement_StockLotResponse buildMovement_StockLotResponse() {
    if (movement_StockLot == null) {
      throw new RuntimeException("Debe pasar la entidad movement_StockLotResponse");
    }
    return new Movement_StockLotResponse(
        movement_StockLot.getId(),
        movement_StockLot.getQuantityTaken(),
        movement_StockLot.getStockLot().getId(),
        movement_StockLot.getStockLot().getBatch()
);  }


}
