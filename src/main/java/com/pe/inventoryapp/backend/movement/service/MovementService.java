package com.pe.inventoryapp.backend.movement.service;

import com.pe.inventoryapp.backend.movement.model.request.MovementRequest;

public interface MovementService {

  // Movimiento hacia una linea de entrega (cuando sale del almacen)
  void movementToDeliveryLine(MovementRequest movementRequest);

  // Movimiento hacia un lote (cuando llega al almacen)
  void movementToStockLot(MovementRequest movementRequest);

}
