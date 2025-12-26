package com.pe.inventoryapp.backend.movement.service;

import com.pe.inventoryapp.backend.movement.model.request.MovementAdjustmentRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementSendRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementTransferRequest;

public interface MovementService {

  // Movimiento hacia una linea de entrega (cuando sale del almacen)
  void saveMovementSend(MovementSendRequest movementSendRequest, Long id_user);
  void saveMovementAdjustment(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user);
  void saveMovementTransfer(MovementTransferRequest movementTransferRequest, Long id_user);
  // Movimiento hacia un lote (cuando llega al almacen)

}
