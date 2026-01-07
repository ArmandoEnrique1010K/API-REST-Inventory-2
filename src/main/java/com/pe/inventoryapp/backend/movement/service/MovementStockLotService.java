package com.pe.inventoryapp.backend.movement.service;

import com.pe.inventoryapp.backend.movement.model.request.MovementAdjustmentRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementReceiveRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementTransferRequest;

public interface MovementStockLotService {
  // Movimientos hacia un lote de stock (cuando entra al almacen)
  void saveMovementReceive(MovementReceiveRequest movementReceiveRequest, Long id_user);
  void saveMovementIncrease(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user);
  void saveMovementDecrease(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user);
  void saveMovementRecovery(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user);
  void saveMovementTransfer(MovementTransferRequest movementTransferRequest, Long id_user);
}
